package api.database.mongodb;

import api.social_media.facebook.FacebookAPI;
import com.mongodb.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDatabaseConnection {

    private String host = "localhost";
    private int port = 27017;
    private MongoClient mongoClient = null;
    private DB database = null;
    private final String DATABASE_NAME = "SocialGraphProcessing";

    public MongoDatabaseConnection() {
        GetDatabaseDetails();
    }

    protected void GetDatabaseDetails() {
        Scanner fileReader = null;
        String host = null;
        int port = -1;

        try {
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:java_resources/mongodb_settings.conf");
            File myFile = resource.getFile();

            fileReader = new Scanner(myFile);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String property = line.substring(0, line.indexOf("="));
                String value = line.substring(line.indexOf("=") + 1);

                switch (property) {
                    case "host":
                        host = value;
                        break;
                    case "port":
                        port = Integer.parseInt(value);
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(FacebookAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        //fileReader.close();
        if (host != null) {
            this.host = host;
        } else {
            System.out.println("No host details");
        }

        if (port == -1) {
            this.port = port;
        } else {
            System.out.println("No port details");
        }
    }

    protected void OpenConnection() {
        try {
            // To connect to mongodb server
            MongoClientOptions options = MongoClientOptions.builder()
                    .socketKeepAlive(true)
                    .connectionsPerHost(100)
                    .connectTimeout(30000)
                    .socketTimeout(60000)
                    .threadsAllowedToBlockForConnectionMultiplier(100)
                    .build();
            mongoClient = new MongoClient(host, port);

            // Now connect to your databases
            database = mongoClient.getDB(DATABASE_NAME);
            System.out.println("Connect to database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    protected void CloseConnection() {
        mongoClient.close();
        mongoClient = null;
        database = null;
    }

    //FACEBOOK PAGES COLLECTION
    protected void AddFacebookPagesCollection() {
        database.createCollection("facebookPages", new BasicDBObject());
    }

    protected boolean InsertIntoFacebookPageCollection(BasicDBObject doc) {
        try {
            DBCollection facebookPageCollection = database.getCollection("facebookPages");
            Thread.sleep(100);
            System.out.println("Insert into facebook pages collections");
            facebookPageCollection.insert(doc);
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    protected List<DBObject> QueryFacebookPageCollection(BasicDBObject query) {
        try {
            DBCollection facebookPageCollection = database.getCollection("facebookPages");
            Thread.sleep(100);
            DBCursor cursor = facebookPageCollection.find(query);
            List<DBObject> results = new ArrayList<>();

            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //FACEBOOK POSTS COLLECTION
    protected void AddFacebookPostsCollection() {
        database.createCollection("facebookPosts", new BasicDBObject());
    }

    protected boolean InsertIntoFacebookPostsCollection(DBObject document) {
        try {
            DBCollection facebookPostCollection = database.getCollection("facebookPosts");
            Thread.sleep(100);
            if (document != null) {
                System.out.println("Insert into facebook posts collections");
                facebookPostCollection.insert(document);
            }
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    protected List<DBObject> QueryFacebookPostsCollection(BasicDBObject query) {
        try {
            DBCollection facebookPostsCollection = database.getCollection("facebookPosts");
            Thread.sleep(100);
            DBCursor cursor = facebookPostsCollection.find(query);
            List<DBObject> results = new ArrayList<>();
            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected List<DBObject> QueryFacebookPostsCollection(BasicDBObject query, int limit) {
        try {
            DBCollection facebookPostsCollection = database.getCollection("facebookPosts");
            Thread.sleep(100);
            DBCursor cursor = facebookPostsCollection.find(query).limit(limit);
            List<DBObject> results = new ArrayList<>();
            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected int CountFacebookPostsCollection(BasicDBObject query) {
        try {
            DBCollection facebookPostsCollection = database.getCollection("facebookPosts");
            Thread.sleep(100);
            int count = (int) facebookPostsCollection.count(query);

            return count;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    protected List<DBObject> GetAllFacebookPosts() {
        try {
            DBCollection facebookPageCollection = database.getCollection("facebookPosts");
            Thread.sleep(100);
            List<DBObject> results = facebookPageCollection.find().toArray();

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //FACEBOOK COMMENTS COLLECTION
    protected void AddFacebookCommentsCollection() {
        database.createCollection("facebookComments", new BasicDBObject());
    }

    protected boolean InsertIntoFacebookCommentsCollection(DBObject document) {
        try {
            DBCollection facebookCommentsCollection = database.getCollection("facebookComments");
            Thread.sleep(100);
            if (document != null) {
                System.out.println("Insert into facebook comments collections");
                facebookCommentsCollection.insert(document);
            }
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    protected List<DBObject> QueryFacebookCommentsCollection(BasicDBObject query) {
        try {
            DBCollection facebookCommentsCollection = database.getCollection("facebookComments");
            Thread.sleep(100);
            DBCursor cursor = facebookCommentsCollection.find(query);
            List<DBObject> results = new ArrayList<>();

            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected List<DBObject> QueryFacebookCommentsCollection(BasicDBObject query, int limit) {
        try {
            DBCollection facebookCommentsCollection = database.getCollection("facebookComments");
            Thread.sleep(100);
            DBCursor cursor = facebookCommentsCollection.find(query).limit(limit);
            List<DBObject> results = new ArrayList<>();

            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected int CountFacebookCommentsCollection(BasicDBObject query) {
        try {
            DBCollection facebookCommentsCollection = database.getCollection("facebookComments");
            Thread.sleep(100);
            int count = (int) facebookCommentsCollection.count(query);

            return count;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    //FACEBOOK REACTIONS COLLECTION
    protected void AddFacebookReactionsCollection() {
        database.createCollection("facebookReactions", new BasicDBObject());
    }

    protected boolean InsertIntoFacebookReactionsCollection(DBObject document) {
        try {
            DBCollection facebookReactionsCollection = database.getCollection("facebookReactions");
            Thread.sleep(100);
            if (document != null) {
                System.out.println("Insert into facebook reactions collections");
                facebookReactionsCollection.insert(document);
            }
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    protected List<DBObject> QueryFacebookReactionsCollection(BasicDBObject query) {
        try {
            DBCollection facebookReactionsCollection = database.getCollection("facebookReactions");
            Thread.sleep(100);
            DBCursor cursor = facebookReactionsCollection.find(query);
            List<DBObject> results = new ArrayList<>();

            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected int CountFacebookReactionsCollection(BasicDBObject query) {
        try {
            DBCollection facebookReactionsCollection = database.getCollection("facebookReactions");
            Thread.sleep(100);
            int count = (int) facebookReactionsCollection.count(query);

            return count;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    //FACEBOOK USERS COLLECTION
    protected void AddFacebookUsersCollection() {
        database.createCollection("facebookUsers", new BasicDBObject());
    }

    protected boolean InsertIntoFacebookUsersCollection(DBObject document) {
        try {
            DBCollection facebookUsersCollection = database.getCollection("facebookUsers");
            Thread.sleep(100);
            if (document != null) {
                System.out.println("Insert into facebook users collections");
                facebookUsersCollection.insert(document);
            }
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    protected List<DBObject> QueryFacebookUsersCollection(BasicDBObject query) {
        try {
            DBCollection facebookUsersCollection = database.getCollection("facebookUsers");
            Thread.sleep(100);
            DBCursor cursor = facebookUsersCollection.find(query);
            List<DBObject> results = new ArrayList<>();

            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected int CountFacebookUsersCollection(BasicDBObject query) {
        try {
            DBCollection facebookUsersCollection = database.getCollection("facebookUsers");
            Thread.sleep(100);
            int count = (int) facebookUsersCollection.count(query);

            return count;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    //TWITTER TWEETS COLLECTION
    protected void AddTwitterTweetsCollection() {
        database.createCollection("twitterTweets", new BasicDBObject());
    }

    protected boolean InsertIntoTwitterTweetsCollection(BasicDBObject document) {
        try {
            DBCollection twitterTweetsCollection = database.getCollection("twitterTweets");
            Thread.sleep(100);
            if (!document.isEmpty()) {
                twitterTweetsCollection.insert(document);
            }
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    protected List<DBObject> QueryTwitterTweetsCollection(BasicDBObject query) {
        try {
            DBCollection twitterTweetsCollection = database.getCollection("twitterTweets");
            Thread.sleep(100);
            DBCursor cursor = twitterTweetsCollection.find(query);
            List<DBObject> results = new ArrayList<>();

            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected List<DBObject> QueryTwitterTweetsCollection(BasicDBObject query, int limit) {
        try {
            DBCollection twitterTweetsCollection = database.getCollection("twitterTweets");
            Thread.sleep(100);
            DBCursor cursor = twitterTweetsCollection.find(query).limit(limit);
            List<DBObject> results = new ArrayList<>();

            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected int CountTwitterTweetsCollection(BasicDBObject query) {
        try {
            DBCollection twitterTweetsCollection = database.getCollection("twitterTweets");
            Thread.sleep(100);
            int count = (int) twitterTweetsCollection.count(query);

            return count;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    protected List<DBObject> GetAllTwitterTweets() {
        try {
            DBCollection twitterTweetsCollection = database.getCollection("twitterTweets");
            Thread.sleep(100);
            List<DBObject> results = twitterTweetsCollection.find().toArray();

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //TWITTER ACCOUNTS COLLECTION
    protected void AddTwitterAccountsCollection() {
        database.createCollection("twitterAccounts", new BasicDBObject());
    }

    protected boolean InsertIntoTwitterAccountsCollection(BasicDBObject document) {
        try {
            DBCollection twitterAccountsCollection = database.getCollection("twitterAccounts");
            Thread.sleep(100);
            if (!document.isEmpty()) {
                twitterAccountsCollection.insert(document);
            }
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    protected List<DBObject> QueryTwitterAccountsCollection(BasicDBObject query) {
        try {
            DBCollection twitterAccountsCollection = database.getCollection("twitterAccounts");
            Thread.sleep(100);
            DBCursor cursor = twitterAccountsCollection.find(query);
            List<DBObject> results = new ArrayList<>();

            if (cursor != null) {
                results = cursor.toArray();
            }

            return results;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected int CountTwitterAccountsCollection(BasicDBObject query) {
        try {
            DBCollection twitterAccountsCollection = database.getCollection("twitterAccounts");
            Thread.sleep(100);
            int count = (int) twitterAccountsCollection.count(query);

            return count;
        } catch (InterruptedException ex) {
            Logger.getLogger(MongoDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
