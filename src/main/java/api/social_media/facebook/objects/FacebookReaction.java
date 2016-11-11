package api.social_media.facebook.objects;

public class FacebookReaction {
    private FacebookUser user;
    private String type;
    private FacebookPost post;

    public FacebookReaction(FacebookUser user, String type, FacebookPost post) {
        this.user = user;
        this.type = type;
        this.post = post;
    }

    public FacebookUser getUser() {
        return user;
    }

    public void setUser(FacebookUser user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FacebookPost getPost() {
        return post;
    }

    public void setPost(FacebookPost post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Reaction{" + "user=" + user + ", type=" + type + ", post=" + post + '}';
    }
}
