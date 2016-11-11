/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.social_media.facebook.objects;

public class FacebookPage {
    private String pageID;
    private String name;

    public FacebookPage(){
        
    }
    
    public FacebookPage(String pageID, String name) {
        this.pageID = pageID;
        this.name = name;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FacebookPage{" + "pageID=" + pageID + ", name=" + name + '}';
    }
}
