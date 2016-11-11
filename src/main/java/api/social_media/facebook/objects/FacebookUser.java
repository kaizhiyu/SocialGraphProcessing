package api.social_media.facebook.objects;

public class FacebookUser {
    private String id;
    private String name;
    private int numberOfFriends;

    public FacebookUser(String id, String name, int numberOfFriends) {
        this.id = id;
        this.name = name;
        this.numberOfFriends = numberOfFriends;
    }
    
    public FacebookUser(String id) {
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

    public int getNumberOfFriends() {
        return numberOfFriends;
    }

    public void setNumberOfFriends(int numberOfFriends) {
        this.numberOfFriends = numberOfFriends;
    }

    @Override
    public String toString() {
        return "LoginUser{" + "id=" + id + ", name=" + name + ", numberOfFriends=" + numberOfFriends + '}';
    }
}
