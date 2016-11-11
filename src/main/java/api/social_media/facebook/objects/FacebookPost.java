package api.social_media.facebook.objects;

public class FacebookPost {
    private String id;
    private String message;
    private String createdTime;
    private String pageID;
    private FacebookUser from;
    private boolean isFeedPost;
    private int numberOfShares;
    private int sentiment = 0;

    public FacebookPost(String id, String message, String createdTime, String pageID, FacebookUser from, boolean isFeedPost, int numberOfShares) {
        this.id = id;
        this.message = message;
        this.createdTime = createdTime;
        this.pageID = pageID;
        this.from = from;
        this.isFeedPost = isFeedPost;
        this.numberOfShares = numberOfShares;
    }
    
    public FacebookPost(String id, String message, String createdTime, String pageID, FacebookUser from, boolean isFeedPost, int numberOfShares, int sentiment) {
        this.id = id;
        this.message = message;
        this.createdTime = createdTime;
        this.pageID = pageID;
        this.from = from;
        this.isFeedPost = isFeedPost;
        this.numberOfShares = numberOfShares;
        this.sentiment = sentiment;
    }
    
    public FacebookPost(String id) {
        this.id = id;
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

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public FacebookUser getFrom() {
        return from;
    }

    public void setFrom(FacebookUser from) {
        this.from = from;
    }

    public boolean isIsFeedPost() {
        return isFeedPost;
    }

    public void setIsFeedPost(boolean isFeedPost) {
        this.isFeedPost = isFeedPost;
    }

    public int getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(int numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }
    
    @Override
    public String toString() {
        return "FacebookPost{" + "id=" + id + ", message=" + message + ", createdTime=" + createdTime + ", pageID=" + pageID + ", from=" + from + ", isFeedPost=" + isFeedPost + ", numberOfShares=" + numberOfShares + '}';
    }
}
