package ui.web.models;

/**
 * Created by kryli on 2016/10/21.
 */
public class Tweet {

    private String tweetText;
    private int id;

    public int getId() {
        return id;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }
}
