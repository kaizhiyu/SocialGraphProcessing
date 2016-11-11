package api.database.neo;

import api.database.mongodb.MongoDatabaseLogic;
import api.social_media.facebook.objects.FacebookPage;
import api.social_media.facebook.objects.FacebookUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NeoDatabaseConnection {

    private GraphDatabaseService db;
    private MongoDatabaseLogic mongoDBLogic;
    private String company;

    private enum RelationshipLabel implements RelationshipType {
        FB_FOLLOWED_BY
    }

    private enum NodeLabel implements Label {
        POST, FB_USER
    }

    public NeoDatabaseConnection(String company) {

        this.company = company;
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader
                .getResource("classpath:java_resources/default.graphdb");
        String filePath = "";
        try {
            File myFile = resource.getFile();
            filePath = myFile.getPath();
            db = dbFactory.newEmbeddedDatabase(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mongoDBLogic = new MongoDatabaseLogic();
    }

    public void generateSocialGraphs(HttpServletRequest request) {


        //String companyID = null;
        mongoDBLogic.OpenConnection();
        FacebookPage page = mongoDBLogic.GetFacebookPage(company);
        mongoDBLogic.CloseConnection();

        String companyID = page.getPageID();

        mongoDBLogic.OpenConnection();
        ArrayList<String> post_ids = mongoDBLogic.GetAllPostIDs(companyID);
        mongoDBLogic.CloseConnection();


        mongoDBLogic.OpenConnection();
        System.out.println("Fetching famous post for "+company+"...");
        String post_id_max = mongoDBLogic.GetFamousPost(post_ids);
        System.out.println("Complete!");
        mongoDBLogic.CloseConnection();

        // create a post node for post_id_max

        try (Transaction tx = db.beginTx()) {
            Node postNode = db.createNode(NodeLabel.POST);
            postNode.setProperty("postID", post_id_max);
            postNode.setProperty("Company", company);

            System.out.println("Created POST node for graph");

            // get list of fb_users who commented or reacted to post and
            // create FB_USER nodes
            mongoDBLogic.OpenConnection();
            System.out.println("Fetching userIDs...");
            ArrayList<String> user_ids = mongoDBLogic
                    .GetUserIDsByPost(post_id_max);
            System.out.println("Complete!");
            mongoDBLogic.CloseConnection();

            mongoDBLogic.OpenConnection();
            System.out.println("Creating FB_USER objects...");
            ArrayList<FacebookUser> fb_users = mongoDBLogic
                    .GetFacebookUsersByPostID(user_ids);
            System.out.println("Complete!");
            mongoDBLogic.CloseConnection();

            int index = 0;
            for (FacebookUser user : fb_users) {
                try {
                    Node userNode = db.createNode(NodeLabel.FB_USER);
                    userNode.setProperty("userID", user.getId());
                    userNode.setProperty("name", user.getName());
                    userNode.setProperty("friends",
                            user.getNumberOfFriends());

                    postNode.createRelationshipTo(userNode,
                            RelationshipLabel.FB_FOLLOWED_BY);

                    if(index++ == 500)
                        break;
                } catch (Exception e) {

                }
            }

            getGraphJSON(post_id_max, request);
            tx.success();

        }


    }


    public void closeDB() {
        db.shutdown();
    }

    public void getGraphJSON(String postID, HttpServletRequest request) {

        // JSONobj should consist of array of nodes and edges

        // Example JSON structure to achieve:
        // {"comment": "Some comment about the graph",
        // "nodes":[{node1},{node2}...{nodeN}],
        // "edges":[{edge1},{edge2}...{edgeN}]}
        // where {nodeK} = {"caption": "friendCOunt", "type": "FB_USER", "id":
        // userID}
        // where {edgeK} = {"source": sourceID, "target": targetID, "caption":
        // "Follows"}
        //
        // nodeK is a JsonObject: [JSONcaption, JSONtype, JSONid]
        // edgeK is a JsonObject: [JSONSource, JSONTarget, JSONCaption]

        // Complete JSON file consists of:
        // 1)comment - comment on contents of the graph
        // 2)JSONNodes - JSONArray of JSONnodeK objects
        // 3)JSONEdges - JSONArray of JSONedgeK objects

        JSONObject jsonFile = new JSONObject();
        JSONArray jsonNodes = new JSONArray();
        JSONArray jsonEdges = new JSONArray();

        JSONObject jsonNodeK;
        JSONObject jsonEdgeK;

        Transaction tx = db.beginTx();
        long pID = Long
                .parseLong(postID.substring(postID.indexOf('_') + 1));
        // add post node to nodes
        jsonNodeK = new JSONObject();// clear the array for new node

        jsonNodeK.put("caption", company);
        jsonNodeK.put("type", "POST");
        jsonNodeK.put("root", true);
        jsonNodeK.put("id", pID);

        jsonNodes.put(jsonNodeK);

        Result r = db.execute("match (n:POST)-[]->(m:FB_USER) WHERE n.Company = '" + company + "' return m");
        ResourceIterator<Node> iterator = r.columnAs("m");

        while (iterator.hasNext()) {
            Node node = iterator.next();// first fill jsonNodes with
            // nodeK
            // objects

            jsonNodeK = new JSONObject();// clear the array for new node

            jsonNodeK.put("caption", node.getProperty("name"));
            jsonNodeK.put("type", "FB_USER");
            jsonNodeK.put("id", Long.parseLong(node.getProperty(
                    "userID").toString()));// if error in
            // future
            // convert this
            // id to Integer

            jsonNodes.put(jsonNodeK);
        }
        jsonFile.put("nodes", jsonNodes);
        jsonFile.put("comment", "Social reach of: " + company);

        r = db
                .execute("MATCH (n:POST)-[r]->(m:FB_USER) WHERE n.Company = '"
                        + company
                        + "' AND n.postID = '"
                        + postID
                        + "' return r, m");

        ResourceIterator<Node> i = r.columnAs("m");
        while (i.hasNext()) {
            long id = Long.parseLong(i.next().getProperty("userID")
                    .toString());
            // System.out.println(id);
            jsonEdgeK = new JSONObject();

            jsonEdgeK.put("source", pID);
            jsonEdgeK.put("target", id);
            jsonEdgeK.put("caption", "follower");

            jsonEdges.put(jsonEdgeK);
        }
        jsonFile.put("edges", jsonEdges);

        String phyPath = request.getSession().getServletContext().getRealPath("/");
        String directory = phyPath + "resources/social_graph";

        File folderDirectory = new File (directory);
        if (!folderDirectory.exists()){
            folderDirectory.mkdir();
        }

        String filepath = directory + "/" + company + ".json";
        File file = new File(filepath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            fw.write(jsonFile.toString());
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        tx.success();
    }

    public void emptyDB() {

        try (Transaction tx = db.beginTx()) {

            db.execute("match p = (n:POST)-[r]->(m:FB_USER) where n.Company = '" + company + "' delete p");
            tx.success();
        }
    }
}
