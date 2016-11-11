package ui.web.models;

import java.util.ArrayList;

public class RegisterUser {

    private String username;
    private String password;
    private String firstname;
    private String surname;
    private ArrayList<String> companyList;

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public ArrayList<String> getCompanyList() {
        return companyList;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = Integer.toString(password.hashCode());
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setCompanyList(ArrayList<String> companyList) {
        this.companyList = companyList;
    }

}
