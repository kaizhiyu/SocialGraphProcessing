package ui.web.models;

import java.util.ArrayList;

public class Identity {

    private String sessionID;
    private String username;
    private ArrayList<String> companyList;
    private boolean isAdmin;

    public Identity(String sessionID, String username, ArrayList<String> companyList, boolean isAdmin) {
        this.sessionID = sessionID;
        this.username = username;
        this.companyList = companyList;
        this.isAdmin = isAdmin;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getCompanyList() {
        return companyList;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCompanyList(ArrayList<String> companyList) {
        this.companyList = companyList;
    }

    public void addCompany(String companyName) {
        companyList.add(companyName);
    }

    public void removeCompany(String companyName) {
        companyList.remove(companyName);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
