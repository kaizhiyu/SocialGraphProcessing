package api.social_media.twitter.objects;

public class TwitterTweet {
    private String id;
    private String message;
    private int retweetCount;
    private int favouriteCount;
    private String createdTime;
    private TwitterAccount account;
    private int sentiment = 0;
    private TwitterAccount about;

    public TwitterTweet(String id, String message, int retweetCount, int favouriteCount, String createdTime, TwitterAccount account, TwitterAccount about) {
        this.id = id;
        this.message = message;
        this.retweetCount = retweetCount;
        this.favouriteCount = favouriteCount;
        this.createdTime = createdTime;
        this.account = account;
        this.about = about;
    }
    
    public TwitterTweet(String id, String message, int retweetCount, int favouriteCount, String createdTime, TwitterAccount account, int sentiment, TwitterAccount about) {
        this.id = id;
        this.message = message;
        this.retweetCount = retweetCount;
        this.favouriteCount = favouriteCount;
        this.createdTime = createdTime;
        this.account = account;
        this.sentiment = sentiment;
        this.about = about;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public TwitterAccount getAccount() {
        return account;
    }

    public void setAccount(TwitterAccount account) {
        this.account = account;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    public TwitterAccount getAbout() {
        return about;
    }

    public void setAbout(TwitterAccount about) {
        this.about = about;
    }

    @Override
    public String toString() {
        return "TwitterTweet{" + "id=" + id + ", message=" + message + ", retweetCount=" + retweetCount + ", favouriteCount=" + favouriteCount + ", createdTime=" + createdTime + ", account=" + account + ", sentiment=" + sentiment + ", about=" + about + '}';
    }
}
