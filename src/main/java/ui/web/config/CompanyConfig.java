/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.web.config;

import api.database.sqlite.SQLiteDatabaseConnection;
import api.social_media.companies.Company;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author kryli
 */
public class CompanyConfig {

    private Company currentCompany;
    private ArrayList<Company> companyList = new ArrayList<>();
    private ArrayList<Company> userCompanyList = new ArrayList<>();

    private final SQLiteDatabaseConnection DB_CONNECTION = new SQLiteDatabaseConnection();

    public CompanyConfig() {
        companyList.clear();
        for (String companyName : DB_CONNECTION.GetAllCompaniesInDatabase()) {
            if(!companyList.contains(companyName))
                companyList.add(new Company(companyName));
        }
    }

    public Company getCurrentCompany() {
        return currentCompany;
    }

    public ArrayList<Company> getCompanyList() {
        return companyList;
    }

    public void setCurrentCompany(Company currentCompany) {
        this.currentCompany = currentCompany;
    }

    public void setUserCompanyList(ArrayList<String> companyList) {
        for (String companyName : companyList) {
            userCompanyList.add(new Company(companyName));
        }
    }

    public void addNewCompanyForUser(String companyName, String username) {
        Company newCompany = new Company(companyName);

        if (!companyList.contains(newCompany)){
            companyList.add(newCompany);
        }
        userCompanyList.add(newCompany);
        DB_CONNECTION.AddNewCompanyForUser(companyName, username);
    }

    public void removeCompanyForUser(String companyName, String username) {
        Company companyToBeDeleted = null;
        for (Company company : userCompanyList) {
            if (company.getCompanyName().equals(companyName)) {
                companyToBeDeleted = company;
                break;
            }
        }
        userCompanyList.remove(companyToBeDeleted);
        DB_CONNECTION.RemoveCompanyForUser(companyName, username);
    }

    public void updateLastDateMinedForCompany(Company company){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        DB_CONNECTION.UpdateLastMinedDateForCompany(company, dateFormat.format(date));
    }

    public String getLastDateMinedForCompany(Company company) {
        return DB_CONNECTION.GetLastMinedDateForCompany(company);
    }

}
