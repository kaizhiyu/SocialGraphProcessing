package api.social_media.companies;

import api.database.mongodb.MongoDatabaseLogic;
import api.social_media.facebook.FacebookAPI;
import api.social_media.facebook.objects.FacebookPage;
import api.social_media.twitter.TwitterAPI;
import api.social_media.twitter.objects.TwitterAccount;

public class Company {
    private String companyName;
    private FacebookPage facebookPage;
    private TwitterAccount twitterAccount;

    public Company(String companyName) {
        MongoDatabaseLogic mongoDBLogic = new MongoDatabaseLogic();
        mongoDBLogic.OpenConnection();

        FacebookPage facebookPage = mongoDBLogic.GetFacebookPage(companyName);
        TwitterAccount twitterAccount = mongoDBLogic.GetTwitterAccountFromName(companyName);

        mongoDBLogic.CloseConnection();

        if (facebookPage == null) {
            FacebookAPI facebookAPI = new FacebookAPI();
            facebookPage = facebookAPI.GetFacebookPage(companyName);

            mongoDBLogic.OpenConnection();
            mongoDBLogic.AddFacebookPage(facebookPage);
            mongoDBLogic.CloseConnection();
        }
        if (twitterAccount == null) {
            TwitterAPI twitterAPI = new TwitterAPI();
            twitterAccount = twitterAPI.GetTwitterAccount(companyName);

            mongoDBLogic.OpenConnection();
            mongoDBLogic.AddTwitterAccount(twitterAccount);
            mongoDBLogic.CloseConnection();
        }

        this.companyName = companyName;
        this.facebookPage = facebookPage;
        this.twitterAccount = twitterAccount;
    }

    public Company(String companyName, FacebookPage facebookPage, TwitterAccount twitterAccount) {
        this.companyName = companyName;
        this.facebookPage = facebookPage;
        this.twitterAccount = twitterAccount;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public FacebookPage getFacebookPage() {
        return facebookPage;
    }

    public void setFacebookPage(FacebookPage facebookPage) {
        this.facebookPage = facebookPage;
    }

    public TwitterAccount getTwitterAccount() {
        return twitterAccount;
    }

    public void setTwitterAccount(TwitterAccount twitterAccount) {
        this.twitterAccount = twitterAccount;
    }

    @Override
    public String toString() {
        return "Company{" + "companyName=" + companyName + ", facebookPage=" + facebookPage + ", twitterAccount=" + twitterAccount + '}';
    }
}
