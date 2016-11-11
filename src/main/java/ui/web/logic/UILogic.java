package ui.web.logic;

import api.database.mongodb.MongoDatabaseLogic;
import api.database.neo.NeoDatabaseConnection;
import api.social_media.companies.Company;
import api.social_media.facebook.FacebookAPI;
import api.social_media.facebook.objects.FacebookPost;
import api.social_media.twitter.TwitterAPI;
import api.social_media.twitter.objects.TwitterTweet;
import authenticator.Authenticator;
import org.springframework.web.servlet.ModelAndView;
import ui.web.config.CompanyConfig;
import ui.web.models.Identity;
import ui.web.models.LoginUser;
import ui.web.models.RegisterUser;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by kryli on 2016/10/14.
 */
public class UILogic {

    private Semaphore dbLocker = new Semaphore(1);

    private final CompanyConfig COMPANY_CONFIG = new CompanyConfig();
    private final MongoDatabaseLogic MONGO_DATABASE_LOGIC = new MongoDatabaseLogic();
    private final Authenticator AUTHENTICATOR = new Authenticator();

    private ArrayList<api.social_media.facebook.objects.FacebookPost> positiveFacebookPostsAndFeedPosts;
    private ArrayList<api.social_media.facebook.objects.FacebookPost> negativeFacebookPostsAndFeedPosts;
    private ArrayList<api.social_media.facebook.objects.FacebookPost> topFacebookPosts;
    private ArrayList<api.social_media.facebook.objects.FacebookPost> topFacebookFeedPosts;

    private ArrayList<api.social_media.twitter.objects.TwitterTweet> positiveTweets;
    private ArrayList<api.social_media.twitter.objects.TwitterTweet> negativeTweets;
    private ArrayList<api.social_media.twitter.objects.TwitterTweet> topTweets;

    private Identity identity;

    private int numberOfFacebookPosts = 5;
    private int numberOfTweets = 5;

    public UILogic() {

    }

    public ModelAndView getBasicBody(HttpServletRequest request) {
        ModelAndView model = new ModelAndView("SiteMaster");

        //region CONFIG DATA

        model.addObject("home", "SocialGraphProcessing");
        model.addObject("companies", getCompanyList());
        model.addObject("userCompanyList", getUserCompanyList());
        model.addObject("currentCompany", COMPANY_CONFIG.getCurrentCompany().getCompanyName());
        model.addObject("sessionID", identity.getSessionID());
        model.addObject("username", identity.getUsername());
        model.addObject("isAdmin", identity.isAdmin());
        model.addObject("lastDateMined", getLastDateMined(COMPANY_CONFIG.getCurrentCompany()));

        //endregion

        //region SUMMARY PAGE DATA

        int totalNumberOfPostsAndTweets = getTotalNumberOfPostsAndTweets();
        int totalPositivePostsAndTweets = getTotalNumberOfPositivePostsAndTweets();
        int totalNeutralPostsAndTweets = getTotalNumberOfNeutralPostsAndTweets();
        int totalNegativePostsAndTweets = getTotalNumberOfNegativePostsAndTweets();

        double totalPositivePostsAndTweetsPercentage = 0.0;
        double totalNeutralPostsAndTweetsPercentage = 0.0;
        double totalNegativePostsAndTweetsPercentage = 0.0;

        if (totalNumberOfPostsAndTweets != 0) {
            totalPositivePostsAndTweetsPercentage = totalPositivePostsAndTweets / (totalNumberOfPostsAndTweets * 1.0);
            totalNeutralPostsAndTweetsPercentage = totalNeutralPostsAndTweets / (totalNumberOfPostsAndTweets * 1.0);
            totalNegativePostsAndTweetsPercentage = totalNegativePostsAndTweets / (totalNumberOfPostsAndTweets * 1.0);
        }

        model.addObject("totalNumberOfPostsAndTweets", totalNumberOfPostsAndTweets);
        model.addObject("totalPositivePostsAndTweets", totalPositivePostsAndTweets);
        model.addObject("totalNeutralPostsAndTweets", totalNeutralPostsAndTweets);
        model.addObject("totalNegativePostsAndTweets", totalNegativePostsAndTweets);
        model.addObject("totalPositivePostsAndTweetsPercentage", totalPositivePostsAndTweetsPercentage);
        model.addObject("totalNeutralPostsAndTweetsPercentage", totalNeutralPostsAndTweetsPercentage);
        model.addObject("totalNegativePostsAndTweetsPercentage", totalNegativePostsAndTweetsPercentage);
        model.addObject("positivePostsAndTweets", getPositivePostsAndTweets());
        model.addObject("negativePostsAndTweets", getNegativePostsAndTweets());

        //endregion

        //region FACEBOOK PAGE DATA

        int facebookPostCount = getFacebookPostCount();
        int facebookFeedPostCount = getFacebookFeedPostCount();
        int facebookPositivePostCount = getNumberOfPositiveFacebookPostsAndFeeds();
        int facebookNeutralPostCount = getNumberOfNeutralFacebookPostsAndFeeds();
        int facebookNegativePostCount = getNumberOfNegativeFacebookPostsAndFeeds();

        double facebookPositivePostPercentage = 0.0;
        double facebookNeutralPostPercentage = 0.0;
        double facebookNegativePostPercentage = 0.0;

        if ((facebookPostCount + facebookFeedPostCount) != 0) {
            facebookPositivePostPercentage = facebookPositivePostCount / (facebookPostCount + facebookFeedPostCount * 1.0);
            facebookNeutralPostPercentage = facebookNeutralPostCount / (facebookPostCount + facebookFeedPostCount * 1.0);
            facebookNegativePostPercentage = facebookNegativePostCount / (facebookPostCount + facebookFeedPostCount * 1.0);
        }

        model.addObject("facebookPostCount", facebookPostCount);
        model.addObject("facebookPositivePostCount", facebookPositivePostCount);
        model.addObject("facebookNeutralPostCount", facebookNeutralPostCount);
        model.addObject("facebookNegativePostCount", facebookNegativePostCount);
        model.addObject("facebookPositivePostPercentage", facebookPositivePostPercentage);
        model.addObject("facebookNeutralPostPercentage", facebookNeutralPostPercentage);
        model.addObject("facebookNegativePostPercentage", facebookNegativePostPercentage);
        model.addObject("facebookFeedPostCount", facebookFeedPostCount);
        model.addObject("positiveFacebookPostsAndFeedPosts", getPositiveFacebookPostsAndFeeds());
        model.addObject("negativeFacebookPostsAndFeedPosts", getNegativeFacebookPostsAndFeeds());
        model.addObject("topFacebookPosts", getTopFacebookPosts());
        model.addObject("topFacebookFeedPosts", getTopFacebookFeedPosts());

        //endregion

        //region TWITTER PAGE DATA

        int tweetCount = getNumberOfTweets();
        int positiveTweetCount = getNumberOfPositiveTweets();
        int neutralTweetCount = getNumberOfNeutralTweets();
        int negativeTweetCount = getNumberOfNegativeTweets();

        double positiveTweetPercentage = 0.0;
        double neutralTweetPercentage = 0.0;
        double negativeTweetPercentage = 0.0;

        if (tweetCount != 0) {
            positiveTweetPercentage = positiveTweetCount / (tweetCount * 1.0);
            neutralTweetPercentage = neutralTweetCount / (tweetCount * 1.0);
            negativeTweetPercentage = negativeTweetCount / (tweetCount * 1.0);
        }

        model.addObject("tweetCount", tweetCount);
        model.addObject("positiveTweetCount", positiveTweetCount);
        model.addObject("neutralTweetCount", neutralTweetCount);
        model.addObject("negativeTweetCount", negativeTweetCount);
        model.addObject("positiveTweetPercentage", positiveTweetPercentage);
        model.addObject("neutralTweetPercentage", neutralTweetPercentage);
        model.addObject("negativeTweetPercentage", negativeTweetPercentage);
        model.addObject("positiveTweets", getPositiveTweets());
        model.addObject("negativeTweets", getNegativeTweets());
        model.addObject("topTweets", getTopTweets());

        //endregion

        return model;
    }

    //region CONFIG METHODS

    public ArrayList<String> getCompanyList() {
        ArrayList<String> companies = new ArrayList<>();

        for (Company company : COMPANY_CONFIG.getCompanyList()) {
            companies.add(company.getCompanyName());
        }

        return companies;
    }

    public ArrayList<String> getUserCompanyList() {
        return identity.getCompanyList();
    }

    public void setCurrentCompany(Company company) {
        COMPANY_CONFIG.setCurrentCompany(company);
    }

    public void removeCompanyForUser(String companyName) {
        COMPANY_CONFIG.removeCompanyForUser(companyName, identity.getUsername());
        identity.removeCompany(companyName);
    }

    public void addNewCompanyForUser(String companyName) {
        COMPANY_CONFIG.addNewCompanyForUser(companyName, identity.getUsername());
        identity.addCompany(companyName);
    }

    public void login(LoginUser user) {
        identity = AUTHENTICATOR.LoginAuthenticator(user);

        COMPANY_CONFIG.setUserCompanyList(identity.getCompanyList());
        COMPANY_CONFIG.setCurrentCompany(new Company(identity.getCompanyList().get(0)));
    }

    public void signOut() {
        identity = null;
    }

    public boolean register(RegisterUser registerUser) {

        return AUTHENTICATOR.RegisterNewUser(registerUser);
    }

    public Identity getIdentity() {
        return identity;
    }

    //endregion

    //region DATA MINING METHODS

    public void getFacebookData(HttpServletRequest request) {
        try {
            dbLocker.acquire();
            FacebookAPI facebook = new FacebookAPI();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            String dateFrom = dateFormat.format(cal.getTime());
            String dateUntil = dateFormat.format(new Date());

            facebook.GetPostsAndFeedsFromPage(COMPANY_CONFIG.getCurrentCompany().getFacebookPage(), dateFrom, dateUntil);
            COMPANY_CONFIG.updateLastDateMinedForCompany(COMPANY_CONFIG.getCurrentCompany());

            NeoDatabaseConnection ndbc = new NeoDatabaseConnection(COMPANY_CONFIG.getCurrentCompany().getCompanyName());
            ndbc.emptyDB();
            ndbc.generateSocialGraphs(request);
            ndbc.closeDB();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

    }

    public void getTwitterData() {
        try {
            dbLocker.acquire();
            TwitterAPI twitter = new TwitterAPI();
            twitter.GetTwitterData(COMPANY_CONFIG.getCurrentCompany().getTwitterAccount());
            COMPANY_CONFIG.updateLastDateMinedForCompany(COMPANY_CONFIG.getCurrentCompany());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }
    }

    public String getLastDateMined(Company company) {

        return COMPANY_CONFIG.getLastDateMinedForCompany(company);
    }

    //endregion

    //region SUMMARY PAGE METHODS

    public int getTotalNumberOfPostsAndTweets() {
        int totalNumberOfPostsAndTweets = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            totalNumberOfPostsAndTweets = MONGO_DATABASE_LOGIC.GetTotalNumberOfPostsAndTweets(COMPANY_CONFIG.getCurrentCompany());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return totalNumberOfPostsAndTweets;
    }

    public int getTotalNumberOfPositivePostsAndTweets() {
        int totalNumberOfPositivePostsAndTweets = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            totalNumberOfPositivePostsAndTweets = MONGO_DATABASE_LOGIC.GetNumberOfPositivePostsAndTweets(COMPANY_CONFIG.getCurrentCompany());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return totalNumberOfPositivePostsAndTweets;
    }

    public int getTotalNumberOfNeutralPostsAndTweets() {
        int totalNumberOfNeutralPostsAndTweets = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            totalNumberOfNeutralPostsAndTweets = MONGO_DATABASE_LOGIC.GetNumberOfNeutralPostsAndTweets(COMPANY_CONFIG.getCurrentCompany());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return totalNumberOfNeutralPostsAndTweets;
    }

    public int getTotalNumberOfNegativePostsAndTweets() {
        int totalNumberOfNegativePostsAndTweets = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            totalNumberOfNegativePostsAndTweets = MONGO_DATABASE_LOGIC.GetNumberOfNegativePostsAndTweets(COMPANY_CONFIG.getCurrentCompany());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return totalNumberOfNegativePostsAndTweets;
    }

    public List<String> getPositivePostsAndTweets() {
        List<String> positivePostsAndTweets = null;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            positivePostsAndTweets = MONGO_DATABASE_LOGIC.GetPositivePostsAndTweets(COMPANY_CONFIG.getCurrentCompany());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return positivePostsAndTweets;
    }

    public List<String> getNegativePostsAndTweets() {
        List<String> negativePostsAndTweets = null;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            negativePostsAndTweets = MONGO_DATABASE_LOGIC.GetNegativePostsAndTweets(COMPANY_CONFIG.getCurrentCompany());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return negativePostsAndTweets;
    }

    //endregion

    //region FACEBOOK PAGE METHODS

    public int getFacebookPostCount() {
        int facebookPostCount = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookPostCount = MONGO_DATABASE_LOGIC.GetNumberOfFacebookPosts(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookPostCount;
    }

    public int getFacebookFeedPostCount() {
        int facebookFeedPostCount = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookFeedPostCount = MONGO_DATABASE_LOGIC.GetNumberOfFacebookFeeds(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookFeedPostCount;
    }

    public int getNumberOfPositiveFacebookPostsAndFeeds() {
        int facebookPositivePostCount = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookPositivePostCount = MONGO_DATABASE_LOGIC.GetNumberOfPositiveFacebookPostsAndFeeds(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookPositivePostCount;
    }

    public int getNumberOfNeutralFacebookPostsAndFeeds() {
        int facebookNeutralPostCount = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookNeutralPostCount = MONGO_DATABASE_LOGIC.GetNumberOfNeutralFacebookPostsAndFeeds(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookNeutralPostCount;
    }

    public int getNumberOfNegativeFacebookPostsAndFeeds() {
        int facebookNegativePostCount = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookNegativePostCount = MONGO_DATABASE_LOGIC.GetNumberOfNegativeFacebookPostsAndFeeds(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookNegativePostCount;
    }

    public int getFacebookCommentCount() {
        int facebookCommentCount = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookCommentCount = MONGO_DATABASE_LOGIC.GetNumberOfCommentsForFacebookPosts(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookCommentCount;
    }

    public int getFacebookReactionCount() {
        int facebookReactionCount = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookReactionCount = MONGO_DATABASE_LOGIC.GetNumberOfReactionsForFacebookPosts(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookReactionCount;
    }

    public int getFacebookShareCount() {
        int facebookShareCount = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookShareCount = MONGO_DATABASE_LOGIC.GetNumberOfSharesFromFacebookPosts(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookShareCount;
    }

    public ArrayList<String> getPositiveFacebookPostsAndFeeds() {
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            positiveFacebookPostsAndFeedPosts = MONGO_DATABASE_LOGIC.GetPositiveFacebookPosts(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return getRandomCollectionOfFacebookPosts(positiveFacebookPostsAndFeedPosts);
    }

    public ArrayList<String> getNegativeFacebookPostsAndFeeds() {
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            negativeFacebookPostsAndFeedPosts = MONGO_DATABASE_LOGIC.GetNegativeFacebookPosts(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return getRandomCollectionOfFacebookPosts(negativeFacebookPostsAndFeedPosts);
    }

    public ArrayList<String> getTopFacebookPosts() {
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            topFacebookPosts = MONGO_DATABASE_LOGIC.GetTopFacebookPosts(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return getRandomCollectionOfFacebookPosts(topFacebookPosts);
    }

    public ArrayList<String> getTopFacebookFeedPosts() {
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            topFacebookFeedPosts = MONGO_DATABASE_LOGIC.GetTopFacebookFeeds(COMPANY_CONFIG.getCurrentCompany().getFacebookPage());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return getRandomCollectionOfFacebookPosts(topFacebookFeedPosts);
    }

    public ArrayList<String> getFacebookPostComments(api.social_media.facebook.objects.FacebookPost facebookPost) {
        ArrayList<String> facebookPostComments = new ArrayList<>();
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            facebookPostComments = MONGO_DATABASE_LOGIC.GetFacebookCommentsForPost(facebookPost);
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return facebookPostComments;
    }

    public ArrayList<String> getRandomCollectionOfFacebookPosts(ArrayList<api.social_media.facebook.objects.FacebookPost> posts) {
        ArrayList<String> facebookPosts = new ArrayList<>();

        while (facebookPosts.size() < Math.min(numberOfFacebookPosts, posts.size())) {
            int randomIndex = (int) (Math.random() * posts.size());
            String message = posts.get(randomIndex).getMessage();
            if (!facebookPosts.contains(message))
                facebookPosts.add(message);
        }

        return facebookPosts;
    }

    public FacebookPost getFacebookPost(String facebookPostText, int id) {
        FacebookPost post = null;

        switch (id) {
            //0 = positive posts
            case 0:
                for (int i = 0; i < positiveFacebookPostsAndFeedPosts.size(); i++) {
                    if (positiveFacebookPostsAndFeedPosts.get(i).getMessage().equals(facebookPostText)) {
                        post = positiveFacebookPostsAndFeedPosts.get(i);
                        break;
                    }
                }
                break;

            //1 = negative posts
            case 1:
                for (int i = 0; i < negativeFacebookPostsAndFeedPosts.size(); i++) {
                    if (negativeFacebookPostsAndFeedPosts.get(i).getMessage().equals(facebookPostText)) {
                        post = negativeFacebookPostsAndFeedPosts.get(i);
                        break;
                    }
                }
                break;

            //2 = top posts
            case 2:
                for (int i = 0; i < topFacebookPosts.size(); i++) {
                    if (topFacebookPosts.get(i).getMessage().equals(facebookPostText)) {
                        post = topFacebookPosts.get(i);
                        break;
                    }
                }
                break;

            //3 = top feed posts
            case 3:
                for (int i = 0; i < topFacebookFeedPosts.size(); i++) {
                    if (topFacebookFeedPosts.get(i).getMessage().equals(facebookPostText)) {
                        post = topFacebookFeedPosts.get(i);
                        break;
                    }
                }
                break;
        }

        return post;
    }

    //endregion

    //region TWITTER PAGE METHODS

    public int getNumberOfTweets() {
        int numberOfTweets = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            numberOfTweets = MONGO_DATABASE_LOGIC.GetNumberOfTwitterTweets(COMPANY_CONFIG.getCurrentCompany().getTwitterAccount());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return numberOfTweets;
    }

    public int getNumberOfPositiveTweets() {
        int numberOfPositiveTweets = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            numberOfPositiveTweets = MONGO_DATABASE_LOGIC.GetNumberOfPositiveTwitterTweets(COMPANY_CONFIG.getCurrentCompany().getTwitterAccount());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return numberOfPositiveTweets;
    }

    public int getNumberOfNeutralTweets() {
        int numberOfNeutralTweets = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            numberOfNeutralTweets = MONGO_DATABASE_LOGIC.GetNumberOfNeutralTwitterTweets(COMPANY_CONFIG.getCurrentCompany().getTwitterAccount());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return numberOfNeutralTweets;
    }

    public int getNumberOfNegativeTweets() {
        int numberOfNegativeTweets = 0;
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            numberOfNegativeTweets = MONGO_DATABASE_LOGIC.GetNumberOfNegativeTwitterTweets(COMPANY_CONFIG.getCurrentCompany().getTwitterAccount());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return numberOfNegativeTweets;
    }

    public ArrayList<String> getPositiveTweets() {
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            positiveTweets = MONGO_DATABASE_LOGIC.GetPositiveTwitterTweets(COMPANY_CONFIG.getCurrentCompany().getTwitterAccount());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return getRandomCollectionOfTweets(positiveTweets);
    }

    public ArrayList<String> getNegativeTweets() {
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            negativeTweets = MONGO_DATABASE_LOGIC.GetNegativeTwitterTweets(COMPANY_CONFIG.getCurrentCompany().getTwitterAccount());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return getRandomCollectionOfTweets(negativeTweets);
    }

    public ArrayList<String> getTopTweets() {
        try {
            dbLocker.acquire();
            MONGO_DATABASE_LOGIC.OpenConnection();
            topTweets = MONGO_DATABASE_LOGIC.GetTopTwitterTweets(COMPANY_CONFIG.getCurrentCompany().getTwitterAccount());
            MONGO_DATABASE_LOGIC.CloseConnection();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dbLocker.release();
        }

        return getRandomCollectionOfTweets(topTweets);
    }

    public ArrayList<String> getRandomCollectionOfTweets(ArrayList<api.social_media.twitter.objects.TwitterTweet> tweetsList) {
        ArrayList<String> tweets = new ArrayList<>();

        while (tweets.size() < Math.min(numberOfTweets, tweetsList.size())) {
            int randomIndex = (int) (Math.random() * tweetsList.size());
            String message = tweetsList.get(randomIndex).getMessage();
            if (!tweets.contains(message))
                tweets.add(message);
        }

        return tweets;
    }

    public TwitterTweet getTweet(String tweetText, int id) {
        TwitterTweet tweet = null;

        switch (id) {
            //0 = positive tweet
            case 0:
                for (int i = 0; i < positiveTweets.size(); i++) {
                    if (positiveTweets.get(i).getMessage().equals(tweetText)) {
                        tweet = positiveTweets.get(i);
                        break;
                    }
                }
                break;

            //1 = negative tweet
            case 1:
                for (int i = 0; i < negativeTweets.size(); i++) {
                    if (negativeTweets.get(i).getMessage().equals(tweetText)) {
                        tweet = negativeTweets.get(i);
                        break;
                    }
                }
                break;

            //2 = top tweet
            case 2:
                for (int i = 0; i < topTweets.size(); i++) {
                    if (topTweets.get(i).getMessage().equals(tweetText)) {
                        tweet = topTweets.get(i);
                        break;
                    }
                }
                break;
        }

        return tweet;
    }

    //endregion
}
