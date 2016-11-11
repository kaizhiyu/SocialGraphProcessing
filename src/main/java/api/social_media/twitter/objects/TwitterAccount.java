package api.social_media.twitter.objects;

public class TwitterAccount {
    private String id;
    private String name;
    private String screenName;
    private int numberOfFollowers;

    public TwitterAccount(String id, String name, String screenName, int numberOfFollowers) {
        this.id = id;
        this.name = name;
        this.screenName = screenName;
        this.numberOfFollowers = numberOfFollowers;
    }
    
    public TwitterAccount(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public int getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(int numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", name=" + name + ", screenName=" + screenName + ", numberOfFollowers=" + numberOfFollowers + '}';
    }
}
