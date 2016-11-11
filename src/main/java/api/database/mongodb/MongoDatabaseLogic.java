package api.database.mongodb;

import api.nlp.stanfordCoreNLP.StanfordCoreNLPAPI;
import api.social_media.companies.Company;
import api.social_media.facebook.objects.*;
import api.social_media.twitter.objects.TwitterAccount;
import api.social_media.twitter.objects.TwitterTweet;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.*;
import java.util.regex.Pattern;

public class MongoDatabaseLogic {

    private MongoDatabaseConnection mongoDatabaseConnection = null;
    private StanfordCoreNLPAPI stanfordCoreNPLAPI = null;

    //Constructor
    public MongoDatabaseLogic() {
        mongoDatabaseConnection = new MongoDatabaseConnection();
        stanfordCoreNPLAPI = new StanfordCoreNLPAPI();
    }

    private void initDatabase() {
        mongoDatabaseConnection.AddFacebookCommentsCollection();
        mongoDatabaseConnection.AddTwitterAccountsCollection();
        mongoDatabaseConnection.AddTwitterTweetsCollection();
        mongoDatabaseConnection.AddFacebookPagesCollection();
        AddFacebookPage(new FacebookPage("22084179046", "Vodacom"));
        mongoDatabaseConnection.AddFacebookPostsCollection();
        mongoDatabaseConnection.AddFacebookReactionsCollection();
        mongoDatabaseConnection.AddFacebookUsersCollection();
    }

    public void OpenConnection() {
        mongoDatabaseConnection.OpenConnection();
    }

    public void CloseConnection() {
        mongoDatabaseConnection.CloseConnection();
    }

    //FACEBOOK
    //FACBOOK PAGE
    //Add facebook page
    public void AddFacebookPage(FacebookPage page) {
        System.out.println("Adding new facebook page: " + page.getName());

        BasicDBObject doc = new BasicDBObject();
        doc.put("pageID", page.getPageID());
        doc.put("name", page.getName());

        boolean success = false;
        do {
            success = mongoDatabaseConnection.InsertIntoFacebookPageCollection(doc);
        } while (success == false);
    }

    //Get ID for facebook page
    public FacebookPage GetFacebookPage(String name) {
        name = name.toLowerCase();
        System.out.println("Getting facebook page ID for: " + name);

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPageCollection(new BasicDBObject("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE)));
        } while (results == null);

        FacebookPage facebookPage = null;

        if (results.size() > 0) {
            facebookPage = new FacebookPage((String) results.get(0).get("pageID"), name);
        }

        return facebookPage;
    }

    //FACEBOOK POSTS
    //Add facebook page
    public void AddFacebookPost(FacebookPost post) {
        DBObject document = null;

        if (!DatabaseContainsFacebookPost(post)) {
            System.out.println("Adding new facebook post: " + post.getId());
            AddFacebookUser(post.getFrom());
            document = new BasicDBObject();
            document.put("postID", post.getId());
            document.put("postMessage", post.getMessage());
            document.put("createdTime", post.getCreatedTime());
            document.put("pageID", post.getPageID());
            document.put("sentiment", GetSentiment(post.getMessage()));
            document.put("isFeed", post.isIsFeedPost());
            document.put("from", post.getFrom().getId());
            document.put("numberOfShares", post.getNumberOfShares());
        }

        boolean success = false;
        do {
            success = mongoDatabaseConnection.InsertIntoFacebookPostsCollection(document);
        } while (success == false);
    }

    //Get all facebook posts
    public ArrayList<FacebookPost> GetAllFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting all posts from facebook");

        ArrayList<FacebookPost> posts = new ArrayList<>();

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(new BasicDBObject("pageID", facebookPage.getPageID()));
        } while (results == null);

        for (DBObject result : results) {
            posts.add(ConvertDBObjectToPost(result, false));
        }

        return posts;
    }

    //Get number of facebook posts
    public int GetNumberOfFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting number of posts from facebook");

        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("isFeed", false);

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountFacebookPostsCollection(query);
        } while (count == -1);

        return count;
    }

    //Get number of facebook posts
    public int GetNumberOfFacebookFeeds(FacebookPage facebookPage) {
        System.out.println("Getting number of feeds from facebook");

        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("isFeed", true);

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountFacebookPostsCollection(query);
        } while (count == -1);

        return count;
    }

    //Contains facebook post
    private boolean DatabaseContainsFacebookPost(FacebookPost post) {
        boolean exist = false;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(new BasicDBObject("postID", post.getId()));
        } while (results == null);

        if (results.size() > 0) {
            exist = true;
        }

        System.out.println("Checking database contains post: " + exist);
        return exist;
    }

    //Get all postsIDs from page
    public ArrayList<String> GetAllPostIDs(String companyID) {
        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(new BasicDBObject("pageID", companyID));
        } while (results == null);

        ArrayList<String> postIDs = new ArrayList<>();

        for (DBObject result : results) {
            postIDs.add((String) result.get("postID"));
        }

        return postIDs;
    }

    //Get all positive posts
    public ArrayList<FacebookPost> GetPositiveFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting all positive posts from facebook");

        ArrayList<FacebookPost> posts = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("sentiment", 1);
        int limit = 10;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(query, limit);
        } while (results == null);

        for (DBObject result : results) {
            posts.add(ConvertDBObjectToPost(result, false));
        }

        return posts;
    }

    //Get number of postive posts
    public int GetNumberOfPositiveFacebookPostsAndFeeds(FacebookPage facebookPage) {
        System.out.println("Getting number of positive posts from facebook");

        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("sentiment", 1);

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountFacebookPostsCollection(query);
        } while (count == -1);

        return count;
    }

    //Get all neutral posts
    public ArrayList<FacebookPost> GetNeutralFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting all neutral posts from facebook");

        ArrayList<FacebookPost> posts = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("sentiment", 0);

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(query);
        } while (results == null);

        for (DBObject result : results) {
            posts.add(ConvertDBObjectToPost(result, false));
        }

        return posts;
    }

    //Get number of neutral posts
    public int GetNumberOfNeutralFacebookPostsAndFeeds(FacebookPage facebookPage) {
        System.out.println("Getting number of neutral posts from facebook");

        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("sentiment", 0);

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountFacebookPostsCollection(query);
        } while (count == -1);

        return count;
    }

    //Get all negative posts
    public ArrayList<FacebookPost> GetNegativeFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting all negative posts from facebook");

        ArrayList<FacebookPost> posts = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("sentiment", -1);
        int limit = 10;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(query, limit);
        } while (results == null);

        for (DBObject result : results) {
            posts.add(ConvertDBObjectToPost(result, false));
        }

        return posts;
    }

    //Get all negative posts
    public int GetNumberOfNegativeFacebookPostsAndFeeds(FacebookPage facebookPage) {
        System.out.println("Getting number of negative posts from facebook");

        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("sentiment", -1);

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountFacebookPostsCollection(query);
        } while (count == -1);

        return count;
    }

    //Get top posts
    public ArrayList<FacebookPost> GetTopFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting top posts from facebook");

        ArrayList<FacebookPost> sortedPosts = new ArrayList<>();

        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("isFeed", false);

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(query);
        } while (results == null);

        for (DBObject result : results) {
            sortedPosts.add(ConvertDBObjectToPost(result, false));
        }

        Collections.sort(sortedPosts, new Comparator() {
            public int compare(Object o1, Object o2) {
                FacebookPost p1 = (FacebookPost) o1;
                FacebookPost p2 = (FacebookPost) o2;

                int v = p2.getNumberOfShares() - p1.getNumberOfShares();

                return v;
            }
        });

        return sortedPosts;
    }

    //Get top feeds
    public ArrayList<FacebookPost> GetTopFacebookFeeds(FacebookPage facebookPage) {
        System.out.println("Getting top feeds from facebook");

        ArrayList<FacebookPost> sortedPosts = new ArrayList<>();

        BasicDBObject query = new BasicDBObject();
        query.put("pageID", facebookPage.getPageID());
        query.put("isFeed", true);

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(query);
        } while (results == null);

        for (DBObject result : results) {
            sortedPosts.add(ConvertDBObjectToPost(result, false));
        }

        Collections.sort(sortedPosts, new Comparator() {
            public int compare(Object o1, Object o2) {
                FacebookPost p1 = (FacebookPost) o1;
                FacebookPost p2 = (FacebookPost) o2;

                int v = p2.getNumberOfShares() - p1.getNumberOfShares();

                return v;
            }
        });

        return sortedPosts;
    }

    //Get facebook post from postID
    public FacebookPost GetFacebookPostFromID(String postID) {
        System.out.println("Getting post from postID");

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookPostsCollection(new BasicDBObject("postID", postID));
        } while (results == null);

        if (results.size() > 0) {
            return ConvertDBObjectToPost(results.get(0), false);
        }

        return null;
    }

    public int GetTotalNumberOfPostsAndFeeds(FacebookPage facebookPage) {
        int numberOfPosts = GetNumberOfFacebookPosts(facebookPage);
        int numberOfFeeds = GetNumberOfFacebookFeeds(facebookPage);

        return numberOfPosts + numberOfFeeds;
    }

    //Get number of shares for posts
    public int GetNumberOfSharesFromFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting number of shares from posts from facebook");
        int countShares = 0;

        ArrayList<FacebookPost> posts = GetAllFacebookPosts(facebookPage);

        for (FacebookPost post : posts) {
            countShares += post.getNumberOfShares();
        }

        return countShares;
    }

    //FACEBOOK COMMENTS
    //Add facebook comments
    public void AddFacebookComments(FacebookComment comment) {
        BasicDBObject document = null;

        if (!DatabaseContainsFacebookComment(comment)) {
            System.out.println("Adding new facebook comment: " + comment.getId());

            AddFacebookUser(comment.getUser());

            document = new BasicDBObject();
            document.put("commentID", comment.getId());
            document.put("postID", comment.getPost().getId());
            document.put("message", comment.getMessage());
            document.put("createdTime", comment.getCreatedTime());
            document.put("userID", comment.getUser().getId());
            document.put("sentiment", GetSentiment(comment.getMessage()));
        }

        boolean success = false;
        do {
            success = mongoDatabaseConnection.InsertIntoFacebookCommentsCollection(document);
        } while (success == false);
    }

    //Contains facebook comments
    private boolean DatabaseContainsFacebookComment(FacebookComment comment) {
        boolean exist = false;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookCommentsCollection(new BasicDBObject("commentID", comment.getId()));
        } while (results == null);

        if (results.size() > 0) {
            exist = true;
        }

        System.out.println("Checking database contains post comment: " + exist);
        return exist;
    }

    //Get all facebook posts
    public ArrayList<String> GetFacebookCommentsForPost(FacebookPost facebookPost) {
        System.out.println("Getting all posts from facebook");
        int numberOfComments = 5;
        ArrayList<String> comments = new ArrayList<>();

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookCommentsCollection(new BasicDBObject("postID", facebookPost.getId()), numberOfComments);
        } while (results == null);

        for (DBObject result : results) {
            comments.add(ConvertDBObjectToComment(result, false).getMessage());
        }

        return comments;
    }

    //Get number of comments for posts
    public int GetNumberOfCommentsForFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting number of comments for posts from facebook");
        int countComments = 0;

        ArrayList<FacebookPost> posts = GetAllFacebookPosts(facebookPage);

        for (FacebookPost post : posts) {
            BasicDBObject query = new BasicDBObject();
            query.put("postID", post.getId());

            int count = -1;
            do {
                count = mongoDatabaseConnection.CountFacebookCommentsCollection(query);
            } while (count == -1);

            countComments += count;
        }

        return countComments;
    }

    //FACEBOOK USERS
    //Add facebook user
    public void AddFacebookUser(FacebookUser user) {
        BasicDBObject document = null;

        if (!DatabaseContainsFacebookUser(user)) {
            System.out.println("Adding new facebook user: " + user.getId());

            document = new BasicDBObject();
            document.put("userID", user.getId());
            document.put("name", user.getName());
            document.put("numberOfFriends", user.getNumberOfFriends());
        }

        boolean success = false;
        do {
            success = mongoDatabaseConnection.InsertIntoFacebookUsersCollection(document);
        } while (success == false);
    }

    //Contains facebook user
    private boolean DatabaseContainsFacebookUser(FacebookUser user) {
        boolean exist = false;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookUsersCollection(new BasicDBObject("userID", user.getId()));
        } while (results == null);

        if (results.size() > 0) {
            exist = true;
        }

        System.out.println("Checking database contains facebook user: " + exist);
        return exist;
    }

    //Get facebook user from user id
    private FacebookUser GetFacebookUser(String userID) {
        System.out.println("Getting facebook user: " + userID);

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookUsersCollection(new BasicDBObject("userID", userID));
        } while (results == null);

        FacebookUser user = null;

        if (results.size() > 0) {
            user = new FacebookUser((String) results.get(0).get("userID"),
                    (String) results.get(0).get("name"),
                    (int) results.get(0).get("numberOfFriends"));
        }

        return user;
    }

    //FACEBOOK REACTIONS
    //Add facebook reaction
    public void AddFacebookReactions(FacebookReaction reaction) {
        BasicDBObject document = null;

        if (!DatabaseContainsFacebookReaction(reaction)) {
            System.out.println("Adding new facebook reaction");

            AddFacebookUser(reaction.getUser());

            document = new BasicDBObject();
            document.put("type", reaction.getType());
            document.put("userID", reaction.getUser().getId());
            document.put("postID", reaction.getPost().getId());
        }

        boolean success = false;
        do {
            success = mongoDatabaseConnection.InsertIntoFacebookReactionsCollection(document);
        } while (success == false);
    }

    //Contains facebook reaction
    private boolean DatabaseContainsFacebookReaction(FacebookReaction reaction) {
        boolean exist = false;

        BasicDBObject query = new BasicDBObject();
        query.put("type", reaction.getType());
        query.put("userID", reaction.getUser().getId());
        query.put("postID", reaction.getPost().getId());

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryFacebookReactionsCollection(query);
        } while (results == null);

        if (results.size() > 0) {
            exist = true;
        }

        System.out.println("Checking database contains post reaction: " + exist);
        return exist;
    }

    //Get number of reactions for posts
    public int GetNumberOfReactionsForFacebookPosts(FacebookPage facebookPage) {
        System.out.println("Getting number of reactions for posts from facebook");
        int countReactions = 0;

        ArrayList<FacebookPost> posts = GetAllFacebookPosts(facebookPage);

        for (FacebookPost post : posts) {
            BasicDBObject query = new BasicDBObject();
            query.put("postID", post.getId());

            int count = -1;
            do {
                count = mongoDatabaseConnection.CountFacebookReactionsCollection(query);
            } while (count == -1);

            countReactions += count;
        }

        return countReactions;
    }

    //TWITTER
    //TWITTER TWEETS
    //Add twitter tweet
    public void AddTwitterTweet(TwitterTweet tweet) {
        BasicDBObject document = new BasicDBObject();

        if (!DatabaseContainsTwitterTweet(tweet)) {
            System.out.println("Adding new twitter tweet: " + tweet.getId());

            AddTwitterAccount(tweet.getAccount());

            document.put("tweetID", tweet.getId());
            document.put("tweetMessage", tweet.getMessage());
            document.put("createdTime", tweet.getCreatedTime());
            document.put("retweetCount", tweet.getRetweetCount());
            document.put("sentiment", GetSentiment(tweet.getMessage()));
            document.put("favouriteCount", tweet.getFavouriteCount());
            document.put("from", tweet.getAccount().getId());
            document.put("about", tweet.getAbout().getId());
        }

        boolean success = false;
        do {
            success = mongoDatabaseConnection.InsertIntoTwitterTweetsCollection(document);
        } while (success == false);
    }

    //Contains twitter tweet
    private boolean DatabaseContainsTwitterTweet(TwitterTweet tweet) {
        boolean exist = false;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterTweetsCollection(new BasicDBObject("tweetID", tweet.getId()));
        } while (results == null);

        if (results.size() > 0) {
            exist = true;
        }

        System.out.println("Checking database contains tweet: " + exist);
        return exist;
    }

    //Get all twitter tweets
    public ArrayList<TwitterTweet> GetAllTwitterTweets(TwitterAccount account) {
        System.out.println("Getting all tweets from twitter");

        ArrayList<TwitterTweet> tweets = new ArrayList<>();

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterTweetsCollection(new BasicDBObject("about", account.getId()));
        } while (results == null);

        for (DBObject result : results) {
            tweets.add(ConvertDBObjectToTweet(result, false));
        }

        return tweets;
    }

    //Get number of twitter tweets
    public int GetNumberOfTwitterTweets(TwitterAccount account) {
        System.out.println("Getting number of tweets from twitter");

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountTwitterTweetsCollection(new BasicDBObject("about", account.getId()));
        } while (count == -1);

        return count;
    }

    //Get all postive tweets
    public ArrayList<TwitterTweet> GetPositiveTwitterTweets(TwitterAccount twitterAccount) {
        System.out.println("Getting all positive tweets from twitter");

        ArrayList<TwitterTweet> tweets = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("about", twitterAccount.getId());
        query.put("sentiment", 1);
        int limit = 10;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterTweetsCollection(query, limit);
        } while (results == null);

        for (DBObject result : results) {
            tweets.add(ConvertDBObjectToTweet(result, false));
        }

        return tweets;
    }

    //Get number of postive tweets
    public int GetNumberOfPositiveTwitterTweets(TwitterAccount twitterAccount) {
        System.out.println("Getting number of positive tweets from twitter");

        BasicDBObject query = new BasicDBObject();
        query.put("about", twitterAccount.getId());
        query.put("sentiment", 1);

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountTwitterTweetsCollection(query);
        } while (count == -1);

        return count;
    }

    //Get all neutral tweets
    public ArrayList<TwitterTweet> GetNeutralTwitterTweets(TwitterAccount twitterAccount) {
        System.out.println("Getting all neutral tweets from twitter");

        ArrayList<TwitterTweet> tweets = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("about", twitterAccount.getId());
        query.put("sentiment", 0);

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterTweetsCollection(query);
        } while (results == null);

        for (DBObject result : results) {
            tweets.add(ConvertDBObjectToTweet(result, false));
        }

        return tweets;
    }

    //Get number of neutral tweets
    public int GetNumberOfNeutralTwitterTweets(TwitterAccount twitterAccount) {
        System.out.println("Getting all neutral tweets from twitter");

        BasicDBObject query = new BasicDBObject();
        query.put("about", twitterAccount.getId());
        query.put("sentiment", 0);

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountTwitterTweetsCollection(query);
        } while (count == -1);

        return count;
    }

    //Get all negative tweets
    public ArrayList<TwitterTweet> GetNegativeTwitterTweets(TwitterAccount twitterAccount) {
        System.out.println("Getting all negative tweets from twitter");

        ArrayList<TwitterTweet> tweets = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("about", twitterAccount.getId());
        query.put("sentiment", -1);
        int limit = 10;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterTweetsCollection(query, limit);
        } while (results == null);

        for (DBObject result : results) {
            tweets.add(ConvertDBObjectToTweet(result, false));
        }

        return tweets;
    }

    //Get all negative tweets
    public int GetNumberOfNegativeTwitterTweets(TwitterAccount twitterAccount) {
        System.out.println("Getting all negative tweets from twitter");

        BasicDBObject query = new BasicDBObject();
        query.put("about", twitterAccount.getId());
        query.put("sentiment", -1);

        int count = -1;
        do {
            count = mongoDatabaseConnection.CountTwitterTweetsCollection(query);
        } while (count == -1);

        return count;
    }

    //Get top posts
    public ArrayList<TwitterTweet> GetTopTwitterTweets(TwitterAccount twitterAccount) {
        System.out.println("Getting top tweets from twitter");

        SortedSet<TwitterTweet> sortedTweets = new TreeSet<>(new Comparator() {
            public int compare(Object o1, Object o2) {
                TwitterTweet p1 = (TwitterTweet) o1;
                TwitterTweet p2 = (TwitterTweet) o2;

                int v = (p2.getRetweetCount()+p2.getFavouriteCount()) - (p1.getRetweetCount()+p1.getFavouriteCount());

                return v;
            }
        }
        );

        BasicDBObject query = new BasicDBObject();
        query.put("about", twitterAccount.getId());

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterTweetsCollection(query);
        } while (results == null);

        for (DBObject result : results) {
            sortedTweets.add(ConvertDBObjectToTweet(result, false));
        }

        return new ArrayList(sortedTweets);
    }

    //TWITTER ACCOUNT
    //Add facebook user
    public void AddTwitterAccount(TwitterAccount account) {
        BasicDBObject doc = new BasicDBObject();

        if (!DatabaseContainsTwitterAccount(account)) {
            System.out.println("Adding new twitter account: " + account.getId());

            doc.put("accountID", account.getId());
            doc.put("name", account.getName());
            doc.put("screenName", account.getScreenName());
            doc.put("numberOfFollowers", account.getNumberOfFollowers());
        }

        boolean success = false;
        do {
            success = mongoDatabaseConnection.InsertIntoTwitterAccountsCollection(doc);
        } while (success == false);
    }

    //Contains facebook user
    private boolean DatabaseContainsTwitterAccount(TwitterAccount account) {
        boolean exist = false;

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterAccountsCollection(new BasicDBObject("accountID", account.getId()));
        } while (results == null);

        if (results.size() > 0) {
            exist = true;
        }

        System.out.println("Checking database contains twitter account: " + exist);
        return exist;
    }

    //Get twitter account from account id
    private TwitterAccount GetTwitterAccountFromID(String accountID) {
        System.out.println("Getting twitter account: " + accountID);

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterAccountsCollection(new BasicDBObject("accountID", accountID));
        } while (results == null);

        TwitterAccount account = null;

        if (results.size() > 0) {
            account = new TwitterAccount((String) results.get(0).get("accountID"),
                    (String) results.get(0).get("name"),
                    (String) results.get(0).get("screenName"),
                    (int) results.get(0).get("numberOfFollowers"));
        }

        return account;
    }

    //Get twitter account from account id
    public TwitterAccount GetTwitterAccountFromName(String name) {
        System.out.println("Getting twitter account: " + name);

        List<DBObject> results = null;
        do {
            results = mongoDatabaseConnection.QueryTwitterAccountsCollection(new BasicDBObject("name", name));
        } while (results == null);

        TwitterAccount account = null;

        if (results.size() > 0) {
            account = new TwitterAccount((String) results.get(0).get("accountID"),
                    (String) results.get(0).get("name"),
                    (String) results.get(0).get("screenName"),
                    (int) results.get(0).get("numberOfFollowers"));
        }

        return account;
    }

    //OTHER DATABASE REQUESTS
    //Get all facebook users that dealt with post
    public ArrayList<String> GetUserIDsByPost(String postID) {
        List<DBObject> reactionResults = null;
        do {
            reactionResults = mongoDatabaseConnection.QueryFacebookReactionsCollection(new BasicDBObject("postID", postID));
        } while (reactionResults == null);

        List<DBObject> commentResults = null;
        do {
            commentResults = mongoDatabaseConnection.QueryFacebookCommentsCollection(new BasicDBObject("postID", postID));
        } while (commentResults == null);

        ArrayList<String> userIDs = new ArrayList<>();

        for (DBObject result : reactionResults) {
            userIDs.add((String) result.get("userID"));
        }
        for (DBObject result : commentResults) {
            userIDs.add((String) result.get("userID"));
        }

        return userIDs;
    }

    public String GetFamousPost(ArrayList<String> ids) {
        String famousPostID = "";
        int highestTotal = 0;

        for (String postID : ids) {
            List<DBObject> reactionResults = null;
            do {
                reactionResults = mongoDatabaseConnection.QueryFacebookReactionsCollection(new BasicDBObject("postID", postID));
            } while (reactionResults == null);

            List<DBObject> commentResults = null;
            do {
                commentResults = mongoDatabaseConnection.QueryFacebookCommentsCollection(new BasicDBObject("postID", postID));
            } while (commentResults == null);

            int totalPopularity = reactionResults.size() + commentResults.size();
            if (totalPopularity > highestTotal) {
                highestTotal = totalPopularity;
                famousPostID = postID;
            }
        }

        return famousPostID;
    }

    public ArrayList<FacebookUser> GetFacebookUsersByPostID(ArrayList<String> userIDs) {
        ArrayList<FacebookUser> facebookUsers = new ArrayList<>();
        Set<String> set = new HashSet<>();

        for (String userID : userIDs) {
            List<DBObject> results = null;
            do {
                results = mongoDatabaseConnection.QueryFacebookUsersCollection(new BasicDBObject("userID", userID));
            } while (results == null);

            if (results.size() > 0) {
                DBObject userResult = results.get(0);
                if(set.add((String) userResult.get("userID"))){
                    FacebookUser facebookUser = new FacebookUser((String) userResult.get("userID"),
                            (String) userResult.get("name"),
                            (int) userResult.get("numberOfFriends"));

                    facebookUsers.add(facebookUser);
                }

            }
        }


        return facebookUsers;
    }

    public int GetTotalNumberOfPostsAndTweets(Company company) {
        int numberOfPosts = GetTotalNumberOfPostsAndFeeds(company.getFacebookPage());
        int numberOfTweets = GetNumberOfTwitterTweets(company.getTwitterAccount());

        return numberOfPosts + numberOfTweets;
    }

    public int GetNumberOfPositivePostsAndTweets(Company company) {
        int positivePosts = GetNumberOfPositiveFacebookPostsAndFeeds(company.getFacebookPage());
        int positiveTweets = GetNumberOfPositiveTwitterTweets(company.getTwitterAccount());

        return positivePosts + positiveTweets;
    }

    public int GetNumberOfNeutralPostsAndTweets(Company company) {
        int neutralPosts = GetNumberOfNeutralFacebookPostsAndFeeds(company.getFacebookPage());
        int neutralTweets = GetNumberOfNeutralTwitterTweets(company.getTwitterAccount());

        return neutralPosts + neutralTweets;
    }

    public int GetNumberOfNegativePostsAndTweets(Company company) {
        int negativePosts = GetNumberOfNegativeFacebookPostsAndFeeds(company.getFacebookPage());
        int negativeTweets = GetNumberOfNegativeTwitterTweets(company.getTwitterAccount());

        return negativePosts + negativeTweets;
    }

    public ArrayList<String> GetPositivePostsAndTweets(Company company) {
        ArrayList<String> postsAndTweetsMessages = new ArrayList<>();

        ArrayList<FacebookPost> positivePosts = GetPositiveFacebookPosts(company.getFacebookPage());
        ArrayList<TwitterTweet> positiveTweets = GetPositiveTwitterTweets(company.getTwitterAccount());

        int numberOfPostsAndTweets = 3;

        if (positivePosts.size() > 0 && positiveTweets.size() > 0) {
            for (int i = 0; i < numberOfPostsAndTweets; i++) {
                int postOrTweet = (int) (Math.random() * 2);
                switch (postOrTweet) {
                    //post
                    case 0:
                        System.out.println("Adding post to postive list");
                        int randomPostsIndex = (int) (Math.random() * positivePosts.size());
                        postsAndTweetsMessages.add(positivePosts.get(randomPostsIndex).getMessage());
                        break;
                    //tweet
                    case 1:
                        System.out.println("Adding tweet to postive list");
                        int randomTweetsIndex = (int) (Math.random() * positiveTweets.size());
                        postsAndTweetsMessages.add(positiveTweets.get(randomTweetsIndex).getMessage());
                        break;
                }
            }
        }

        return postsAndTweetsMessages;
    }

    public ArrayList<String> GetNegativePostsAndTweets(Company company) {
        ArrayList<String> postsAndTweetsMessages = new ArrayList<>();

        ArrayList<FacebookPost> negativePosts = GetNegativeFacebookPosts(company.getFacebookPage());
        ArrayList<TwitterTweet> negativeTweets = GetNegativeTwitterTweets(company.getTwitterAccount());

        int numberOfPostsAndTweets = 3;

        if (negativePosts.size() > 0 && negativeTweets.size() > 0) {
            for (int i = 0; i < numberOfPostsAndTweets; i++) {
                int postOrTweet = (int) (Math.random() * 2);
                switch (postOrTweet) {
                    //post
                    case 0:
                        System.out.println("Adding post to negative list");
                        int randomPostsIndex = (int) (Math.random() * negativePosts.size());
                        postsAndTweetsMessages.add(negativePosts.get(randomPostsIndex).getMessage());
                        break;
                    //tweet
                    case 1:
                        System.out.println("Adding tweet to negative list");
                        int randomTweetsIndex = (int) (Math.random() * negativeTweets.size());
                        postsAndTweetsMessages.add(negativeTweets.get(randomTweetsIndex).getMessage());
                        break;
                }
            }
        }

        return postsAndTweetsMessages;
    }

    //NLP TOOLS
    //Get sentiment
    private int GetSentiment(String text) {
        int sentiment = stanfordCoreNPLAPI.findSentiment(text);
        if (sentiment <= 1) {
            return -1;
        } else if (sentiment == 2) {
            return 0;
        }

        return 1;
    }

    //TOOLS
    private FacebookPost ConvertDBObjectToPost(DBObject dbObject, boolean addAccountDetails) {
        String postID = (String) dbObject.get("postID");
        String postMessage = (String) dbObject.get("postMessage");
        String createdTime = (String) dbObject.get("createdTime");
        String pageID = (String) dbObject.get("pageID");
        int sentiment = (int) dbObject.get("sentiment");
        boolean isFeed = (boolean) dbObject.get("isFeed");
        String userID = (String) dbObject.get("from");
        int numberOfShares = (int) dbObject.get("numberOfShares");
        if (numberOfShares == -1) {
            numberOfShares = 0;
        }
        FacebookUser user;

        if (addAccountDetails) {
            user = GetFacebookUser(userID);
        } else {
            user = new FacebookUser(userID);
        }

        FacebookPost post = new FacebookPost(postID,
                postMessage,
                createdTime,
                pageID,
                user,
                isFeed,
                numberOfShares,
                sentiment);

        return post;
    }

    private FacebookComment ConvertDBObjectToComment(DBObject dbObject, boolean addDetails) {
        String commentID = (String) dbObject.get("commentID");
        String message = (String) dbObject.get("message");
        String createdTime = (String) dbObject.get("createdTime");
        String postID = (String) dbObject.get("postID");
        int sentiment = (int) dbObject.get("sentiment");
        String userID = (String) dbObject.get("userID");

        FacebookUser user = null;
        FacebookPost post = null;

        if (addDetails) {
            user = GetFacebookUser(userID);
            post = GetFacebookPostFromID(postID);
        } else {
            user = new FacebookUser(userID);
            post = new FacebookPost(postID);
        }

        FacebookComment comment = new FacebookComment(createdTime, user, message, commentID, post);

        return comment;
    }

    private TwitterTweet ConvertDBObjectToTweet(DBObject dbObject, boolean addAccountDetails) {
        String tweetID = (String) dbObject.get("tweetID");
        String tweetMessage = (String) dbObject.get("tweetMessage");
        String createdTime = (String) dbObject.get("createdTime");
        int retweetCount = (int) dbObject.get("retweetCount");
        int sentiment = (int) dbObject.get("sentiment");
        int favouriteCount = (int) dbObject.get("favouriteCount");
        String accountID = (String) dbObject.get("from");
        String aboutID = (String) dbObject.get("about");

        TwitterAccount accountFrom;
        TwitterAccount accountAbout;

        if (addAccountDetails) {
            accountFrom = GetTwitterAccountFromID(accountID);
            accountAbout = GetTwitterAccountFromID(aboutID);
        } else {
            accountFrom = new TwitterAccount(accountID);
            accountAbout = new TwitterAccount(aboutID);
        }

        TwitterTweet tweet = new TwitterTweet(tweetID,
                tweetMessage,
                retweetCount,
                favouriteCount,
                createdTime,
                accountFrom,
                sentiment,
                accountAbout);

        return tweet;
    }
}
