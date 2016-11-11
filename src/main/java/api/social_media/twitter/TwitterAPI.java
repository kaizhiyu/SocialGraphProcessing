package api.social_media.twitter;

import api.database.mongodb.MongoDatabaseLogic;
import api.social_media.twitter.objects.TwitterAccount;
import api.social_media.twitter.objects.TwitterTweet;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TwitterAPI {

    private final Twitter twitter;
    private MongoDatabaseLogic mongoDBLogic;

    public TwitterAPI() {
        twitter = new TwitterFactory(GetLoginDetails().build()).getInstance();
        mongoDBLogic = new MongoDatabaseLogic();
    }

    private ConfigurationBuilder GetLoginDetails() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        Scanner fileReader = null;
        String consumerKey = null;
        String consumerSecret = null;
        String accessToken = null;
        String tokenSecret = null;

        try {
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:java_resources/twitter_settings.conf");
            File myFile = resource.getFile();

            fileReader = new Scanner(myFile);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String property = line.substring(0, line.indexOf("="));
                String value = line.substring(line.indexOf("=") + 1);

                switch (property) {
                    case "consumerKey":
                        consumerKey = value;
                        break;
                    case "consumerSecret":
                        consumerSecret = value;
                        break;
                    case "accessToken":
                        accessToken = value;
                        break;
                    case "accessTokenSecret":
                        tokenSecret = value;
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(TwitterAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        fileReader.close();

        if (consumerKey != null && consumerSecret != null && accessToken != null && tokenSecret != null) {
            configurationBuilder.setOAuthConsumerKey(consumerKey);
            configurationBuilder.setOAuthConsumerSecret(consumerSecret);
            configurationBuilder.setOAuthAccessToken(accessToken);
            configurationBuilder.setOAuthAccessTokenSecret(tokenSecret);
        } else {
            System.out.println("No access details");
        }

        return configurationBuilder;
    }

    public void GetTwitterData(TwitterAccount twitterAccount) {
        System.out.println("Getting tweets for: " + twitterAccount.getName());
        ArrayList<TwitterTweet> tweets = GetTweetsAboutBusiness(twitterAccount);

        mongoDBLogic.OpenConnection();
        for (TwitterTweet tweet : tweets) {
            mongoDBLogic.AddTwitterTweet(tweet);
        }
        mongoDBLogic.CloseConnection();
    }

    public TwitterAccount GetTwitterAccount(String companyName) {
        TwitterAccount account = null;

        try {
            ResponseList<User> users = twitter.searchUsers(companyName, 1);
            for (User user : users) {
                if (user.getStatus() != null) {
                    if (user.getName().equalsIgnoreCase(companyName)) {
                        account = new TwitterAccount("" + user.getId(),
                                companyName,
                                user.getScreenName(),
                                user.getFollowersCount());
                        break;
                    }
                }
            }
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        return account;
    }

    private ArrayList<TwitterTweet> GetTweetsAboutBusiness(TwitterAccount twitterAccount) {
        ArrayList<TwitterTweet> tweets = new ArrayList<>();
        long maxID = -1;

        try {
            Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");

            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

            for (int queryNumber = 0; queryNumber < 450; queryNumber++) {
                if (searchTweetsRateLimit.getRemaining() == 0) {
                    Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset() + 2) * 10001);
                }

                Query query = new Query(twitterAccount.getName());
                query.setCount(100);
                query.setLang("en");

                if (maxID != -1) {
                    query.setMaxId(maxID - 1);
                }

                QueryResult result = twitter.search(query);

                if ((result.getTweets().size()) == 0) {
                    break;
                }

                for (Status status : result.getTweets()) {
                    String id = "" + status.getId();
                    String message = status.getText();
                    message = message.replaceAll("'", "");
                    message = removeNewLinesFromString(message);
                    int retweetCount = status.getRetweetCount();
                    String createdTime = "" + status.getCreatedAt();
                    int favouriteCount = status.getFavoriteCount();

                    String userID = "" + status.getUser().getId();
                    String userName = status.getUser().getName();
                    userName = userName.replaceAll("'", "");
                    String userScreenName = status.getUser().getScreenName();
                    userScreenName = userScreenName.replaceAll("'", "");
                    int userNumberOfFollowers = status.getUser().getFollowersCount();

                    TwitterTweet tweet = new TwitterTweet(id,
                            message,
                            retweetCount,
                            favouriteCount,
                            createdTime,
                            new TwitterAccount(userID, userName, userScreenName, userNumberOfFollowers),
                            twitterAccount);

                    
                    tweets.add(tweet);

                    if (maxID == -1 || status.getId() < maxID) {
                        maxID = status.getId();
                    }
                }
                searchTweetsRateLimit = result.getRateLimitStatus();
            }
        } catch (TwitterException | InterruptedException ex) {
            Logger.getLogger(TwitterAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Total tweets found: " + tweets.size());
        return tweets;
    }

    private String removeNewLinesFromString(String message){
        return message.trim().replaceAll("[\\t\\n\\r]+", " ");
    }
}
