/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.web.models;

/**
 * @author kryli
 */
public class CompanyModel {

    private String companyName;

    public CompanyModel() {
    }

    public CompanyModel(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

}
