package api.social_media.facebook.objects;

public class FacebookComment {
    private String createdTime;
    private FacebookUser user;
    private String message;
    private String id;
    private FacebookPost post;

    public FacebookComment(String createdTime, FacebookUser user, String message, String id, FacebookPost post) {
        this.createdTime = createdTime;
        this.user = user;
        this.message = message;
        this.id = id;
        this.post = post;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public FacebookUser getUser() {
        return user;
    }

    public void setUser(FacebookUser user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FacebookPost getPost() {
        return post;
    }

    public void setPost(FacebookPost post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Comment{" + "createdTime=" + createdTime + ", user=" + user + ", message=" + message + ", id=" + id + ", post=" + post + '}';
    }
}
