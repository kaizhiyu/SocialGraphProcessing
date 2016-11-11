package api.social_media.facebook;

import api.database.mongodb.MongoDatabaseLogic;
import api.social_media.facebook.objects.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

//https://graph.facebook.com/22084179046_10154619531149047/reactions?limit=1000&access_token=231764403893586|mwsffxveb5d11UQkttqjwjm3ToE
//https://graph.facebook.com/22084179046/posts?&fields=reactions&until=2016-09-30&since=2016-09-01&access_token=231764403893586|mwsffxveb5d11UQkttqjwjm3ToE
//https://graph.facebook.com/129888297470926/friends?&access_token=231764403893586%7Cmwsffxveb5d11UQkttqjwjm3ToE
public class FacebookAPI {

    private String accessTokenString = null;
    private MongoDatabaseLogic mongoDBLogic;

    public FacebookAPI() {
        GetLoginDetails();
        mongoDBLogic = new MongoDatabaseLogic();
    }

    private void GetLoginDetails() {
        Scanner fileReader = null;
        String accessToken = null;

        try {
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:java_resources/facebook_settings.conf");
            File myFile = resource.getFile();

            fileReader = new Scanner(myFile);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String property = line.substring(0, line.indexOf("="));
                String value = line.substring(line.indexOf("=") + 1);

                switch (property) {
                    case "accessToken":
                        accessToken = value;
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file found: " + e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(FacebookAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        //fileReader.close();
        if (accessToken != null) {
            accessTokenString = accessToken;
        } else {
            System.out.println("No access details");
        }
    }

    public FacebookPage GetFacebookPage(String companyName) {
        System.out.println("Getting page ID for: " + companyName);
        String comapanyNameURLVersion = companyName.replaceAll(" ", "%20");
        URL url = null;
        JSONArray jsonArr = null;
        JSONObject jsonObj;
        FacebookPage page = null;

        try {
            url = new URL("https://graph.facebook.com/search?q=" + comapanyNameURLVersion + "&type=page&fields=id,name&limit=10&access_token=" + accessTokenString);

            URLConnection con = url.openConnection();
            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                content = reader.readLine();
            }

            jsonObj = new JSONObject(content);

            jsonArr = jsonObj.getJSONArray("data");

            for (int i = 0; i < jsonArr.length(); i++) {
                try {
                    if (jsonArr.getJSONObject(i).has("id") && jsonArr.getJSONObject(i).has("name")) {
                        String id = jsonArr.getJSONObject(i).getString("id");
                        String name = jsonArr.getJSONObject(i).getString("name");

                        if (name.equalsIgnoreCase(companyName)) {
                            page = new FacebookPage(id,
                                    name);
                            System.out.println("Found page ID");
                            break;
                        }

                    }
                } catch (JSONException e) {
                    System.out.println("No data found: " + e.getMessage());
                }
            }
        } catch (MalformedURLException e1) {
            System.out.println("No data avaliable: " + e1.getMessage());
        } catch (IOException | JSONException e) {
            System.out.println("No data avaliable: " + e.getMessage());
        }

        return page;
    }

    public void GetPostsAndFeedsFromPage(FacebookPage facebookPage, String dateFrom, String dateTo) {
        ArrayList<FacebookPost> posts = GetPostsFromPage(facebookPage.getPageID(), dateFrom, dateTo);
        ArrayList<FacebookPost> feeds = GetFeedsFromPage(facebookPage.getPageID(), dateFrom, dateTo);

        posts.addAll(feeds);
        System.out.println("Total number of facebook posts and feeds: " + posts.size());

        ArrayList<FacebookReaction> reactions;
        ArrayList<FacebookComment> comments;

        for (FacebookPost post : posts) {
            reactions = GetReactionsForPost(post);
            comments = GetCommentsForPost(post);

            mongoDBLogic.OpenConnection();

            mongoDBLogic.AddFacebookPost(post);
            for (FacebookReaction reaction : reactions) {
                mongoDBLogic.AddFacebookReactions(reaction);
            }
            for (FacebookComment comment : comments) {
                mongoDBLogic.AddFacebookComments(comment);
            }

            mongoDBLogic.CloseConnection();
        }

    }

    private ArrayList<FacebookPost> GetPostsFromPage(String pageID, String dateFrom, String dateTo) {
        System.out.println("Getting posts for page: " + pageID);

        URL url = null;
        JSONArray jsonArr = null;
        JSONObject jsonObj;
        ArrayList<FacebookPost> posts = new ArrayList<>();

        try {
            url = new URL("https://graph.facebook.com/" + pageID + "/posts?&fields=from,message,created_time,shares&limit=100&until=" + dateTo + "&since=" + dateFrom
                    + "&access_token=" + accessTokenString);

            URLConnection con = url.openConnection();
            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                content = reader.readLine();
            }

            jsonObj = new JSONObject(content);

            jsonArr = jsonObj.getJSONArray("data");

            for (int i = 0; i < jsonArr.length(); i++) {
                try {
                    if (jsonArr.getJSONObject(i).has("id") && jsonArr.getJSONObject(i).has("message")
                            && jsonArr.getJSONObject(i).has("created_time")) {

                        String id = jsonArr.getJSONObject(i).getString("id");
                        String message = jsonArr.getJSONObject(i).getString("message");
                        message = message.replaceAll("'", "");
                        message = removeNewLinesFromString(message);
                        String createdTime = jsonArr.getJSONObject(i).getString("created_time");

                        JSONObject from = jsonArr.getJSONObject(i).getJSONObject("from");
                        String userName = from.getString("name");
                        userName = userName.replaceAll("'", "");
                        String userID = from.getString("id");
                        int userNumberOfFriends = -1;
                        if (!userID.equals(pageID)) {
                            userNumberOfFriends = GetNumberOfFriendsForUser(userID);
                        } else {
                            System.out.println("No data found (friends)");
                        }

                        JSONObject shares = jsonArr.getJSONObject(i).getJSONObject("shares");
                        int numberOfShares = shares.getInt("count");

                        FacebookPost post = new FacebookPost(id,
                                message,
                                createdTime,
                                pageID,
                                new FacebookUser(userID, userName, userNumberOfFriends),
                                false,
                                numberOfShares);

                        if (!ArraListContainsPost(posts, post)) {
                            posts.add(post);
                        }
                    }
                } catch (JSONException e) {
                    System.out.println("No data found: " + e.getMessage());
                }
            }
        } catch (MalformedURLException e1) {
            System.out.println("No data avaliable: " + e1.getMessage());
        } catch (IOException | JSONException e) {
            System.out.println("No data avaliable: " + e.getMessage());
        }

        System.out.println("Found " + posts.size() + " posts");
        return posts;
    }

    private ArrayList<FacebookPost> GetFeedsFromPage(String pageID, String dateFrom, String dateTo) {
        System.out.println("Getting feeds for page: " + pageID);

        URL url = null;
        JSONArray jsonArr = null;
        JSONObject jsonObj;
        ArrayList<FacebookPost> feeds = new ArrayList<>();

        try {
            url = new URL("https://graph.facebook.com/" + pageID + "/feed?&fields=from,message,created_time&limit=100&until=" + dateTo + "&since=" + dateFrom
                    + "&access_token=" + accessTokenString);

            URLConnection con = url.openConnection();
            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                content = reader.readLine();
            }

            jsonObj = new JSONObject(content);

            jsonArr = jsonObj.getJSONArray("data");

            for (int i = 0; i < jsonArr.length(); i++) {
                try {
                    if (jsonArr.getJSONObject(i).has("id") && jsonArr.getJSONObject(i).has("message")
                            && jsonArr.getJSONObject(i).has("created_time") && jsonArr.getJSONObject(i).has("from")) {

                        String id = jsonArr.getJSONObject(i).getString("id");
                        String message = jsonArr.getJSONObject(i).getString("message");
                        message = message.replaceAll("'", "");
                        message = removeNewLinesFromString(message);
                        String createdTime = jsonArr.getJSONObject(i).getString("created_time");

                        JSONObject from = jsonArr.getJSONObject(i).getJSONObject("from");
                        String userName = from.getString("name");
                        String userID = from.getString("id");
                        int userNumberOfFriends = GetNumberOfFriendsForUser(userID);

                        int numberOfShares = -1;

                        FacebookPost feed = new FacebookPost(id,
                                message,
                                createdTime,
                                pageID,
                                new FacebookUser(userID, userName, userNumberOfFriends),
                                true,
                                numberOfShares);

                        if (!ArraListContainsPost(feeds, feed)) {
                            feeds.add(feed);
                        }
                    }
                } catch (JSONException e) {
                    System.out.println("No data found: " + e.getMessage());
                }
            }
        } catch (MalformedURLException e1) {
            System.out.println("No data found: " + e1.getMessage());
        } catch (IOException | JSONException e) {
            System.out.println("No data found: " + e.getMessage());
        }

        System.out.println("Found " + feeds.size() + " feeds");
        return feeds;
    }

    private ArrayList<FacebookReaction> GetReactionsForPost(FacebookPost post) {
        System.out.println("Getting reactions for post: " + post.getId());

        URL url = null;
        JSONArray jsonArr = null;
        JSONObject jsonObj;
        ArrayList<FacebookReaction> reactions = new ArrayList<>();

        try {
            url = new URL("https://graph.facebook.com/" + post.getId() + "/reactions?&limit=100&access_token=" + accessTokenString);

            URLConnection con = url.openConnection();
            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                content = reader.readLine();
            }

            jsonObj = new JSONObject(content);

            jsonArr = jsonObj.getJSONArray("data");

            for (int i = 0; i < jsonArr.length(); i++) {
                try {
                    if (jsonArr.getJSONObject(i).has("id") && jsonArr.getJSONObject(i).has("name")
                            && jsonArr.getJSONObject(i).has("type")) {

                        String userID = jsonArr.getJSONObject(i).getString("id");
                        String userName = jsonArr.getJSONObject(i).getString("name");
                        userName = userName.replaceAll("'", "");
                        int userNumberOfFriends = GetNumberOfFriendsForUser(userID);

                        String type = jsonArr.getJSONObject(i).getString("type");

                        FacebookReaction reaction = new FacebookReaction(new FacebookUser(userID, userName, userNumberOfFriends),
                                type,
                                post);

                        if (!ArraListContainsReaction(reactions, reaction)) {
                            reactions.add(reaction);
                        }
                    }
                } catch (JSONException e) {
                    System.out.println("No data found: " + e.getMessage());
                }
            }
        } catch (MalformedURLException e1) {
            System.out.println("No data found: " + e1.getMessage());
        } catch (IOException | JSONException e) {
            System.out.println("No data found: " + e.getMessage());
        }

        return reactions;
    }

    private ArrayList<FacebookComment> GetCommentsForPost(FacebookPost post) {
        System.out.println("Getting comments for post: " + post.getId());

        URL url = null;
        JSONArray jsonArr = null;
        JSONObject jsonObj;
        ArrayList<FacebookComment> comments = new ArrayList<>();

        try {
            url = new URL("https://graph.facebook.com/" + post.getId() + "/comments?&limit=100&access_token=" + accessTokenString);

            URLConnection con = url.openConnection();
            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                content = reader.readLine();
            }

            jsonObj = new JSONObject(content);

            jsonArr = jsonObj.getJSONArray("data");

            for (int i = 0; i < jsonArr.length(); i++) {
                try {
                    if (jsonArr.getJSONObject(i).has("created_time") && jsonArr.getJSONObject(i).has("message") && jsonArr.getJSONObject(i).has("id")) {
                        JSONObject from = jsonArr.getJSONObject(i).getJSONObject("from");

                        String createdTime = jsonArr.getJSONObject(i).getString("created_time");
                        String message = jsonArr.getJSONObject(i).getString("message");
                        message = message.replaceAll("'", "");
                        String id = jsonArr.getJSONObject(i).getString("id");

                        String userName = from.getString("name");
                        userName = userName.replaceAll("'", "");
                        String userID = from.getString("id");
                        int numberOfFriendsUser = GetNumberOfFriendsForUser(userID);

                        FacebookComment comment = new FacebookComment(createdTime,
                                new FacebookUser(userID, userName, numberOfFriendsUser),
                                message,
                                id,
                                post);

                        if (!ArraListContainsComment(comments, comment)) {
                            comments.add(comment);
                        }
                    }
                } catch (JSONException e) {
                    System.out.println("No data found: " + e.getMessage());
                }
            }
        } catch (MalformedURLException e1) {
            System.out.println("No data found: " + e1.getMessage());
        } catch (IOException | JSONException e) {
            System.out.println("No data found: " + e.getMessage());
        }

        return comments;
    }

    private int GetNumberOfFriendsForUser(String userID) {
        System.out.println("Getting number of friends for user: " + userID);

        URL url = null;
        JSONObject jsonObj;
        int numberOfFriends = -1;

        try {
            url = new URL("https://graph.facebook.com/" + userID + "/friends?access_token=" + accessTokenString);

            URLConnection con = url.openConnection();
            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                content = reader.readLine();
            }

            jsonObj = new JSONObject(content);

            JSONObject summary = jsonObj.getJSONObject("summary");
            numberOfFriends = summary.getInt("total_count");

        } catch (MalformedURLException e1) {
            System.out.println("No data found: " + e1.getMessage());
        } catch (IOException | JSONException e) {
            System.out.println("No data found: " + e.getMessage());
        }

        return numberOfFriends;
    }

    private boolean ArraListContainsPost(ArrayList<FacebookPost> posts, FacebookPost post) {
        for (int i = 0; i < posts.size(); i++) {
            FacebookPost temp = posts.get(i);
            if (temp.getId().equals(post.getId())) {
                return true;
            }
        }

        return false;
    }

    private boolean ArraListContainsComment(ArrayList<FacebookComment> comments, FacebookComment comment) {
        for (int i = 0; i < comments.size(); i++) {
            FacebookComment temp = comments.get(i);
            if (temp.getId().equals(comment.getId())) {
                return true;
            }
        }

        return false;
    }

    private boolean ArraListContainsReaction(ArrayList<FacebookReaction> reactions, FacebookReaction reaction) {
        for (int i = 0; i < reactions.size(); i++) {
            FacebookReaction temp = reactions.get(i);
            if ((temp.getPost()).equals(reaction.getPost()) && temp.getType().equals(reaction.getType()) && temp.getUser().equals(reaction.getUser())) {
                return true;
            }
        }

        return false;
    }

    private String removeNewLinesFromString(String message) {
        return message.trim().replaceAll("[\\t\\n\\r]+", " ");
    }
}
