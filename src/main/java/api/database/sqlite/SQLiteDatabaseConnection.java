package api.database.sqlite;

import api.social_media.companies.Company;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ui.web.models.LoginUser;
import ui.web.models.RegisterUser;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SQLiteDatabaseConnection {

    private DriverManagerDataSource dataSource;

    public SQLiteDatabaseConnection() {
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");

        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("classpath:java_resources/authentication_database.sqlite");
        String filePath = "";
        try {
            File myFile = resource.getFile();
            filePath = myFile.getPath();
        } catch (IOException ex) {
            Logger.getLogger(SQLiteDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        dataSource.setUrl("jdbc:sqlite:" + filePath);
    }

    public boolean AddNewUser(RegisterUser user) {

        Statement stmt = null;
        Connection connection = null;
        boolean success = false;

        try {
            connection = dataSource.getConnection();

            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            stmt.executeUpdate("INSERT INTO Users (name, surname, username, password, isAdmin) "
                    + "VALUES ('" + user.getFirstname() + "', '" + user.getSurname() + "', '" + user.getUsername() + "', '" + user.getHashedPassword() + "', 0);");

            connection.commit();

            for (String companyName : user.getCompanyList()) {
                AddNewCompanyForUser(companyName, user.getUsername());
            }

            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean DatabaseContainsUser(RegisterUser user) {
        Statement stmt = null;
        boolean exists = false;
        ResultSet rs = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT * FROM Users WHERE username = '" + user.getUsername() + "';");

            if (rs.next()) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    public boolean CorrectLoginDetails(LoginUser user) {
        Statement stmt = null;
        boolean exists = false;
        ResultSet rs = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT * FROM Users WHERE username = '" + user.getUsername() + "' AND password = '" + user.getHashedPassword() + "';");

            if (rs.next()) {
                exists = true;
            } else {
                exists = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    public int MakeNewSession(LoginUser user) {
        int userID = GetUserIDForUser(user);
        int sessionID = 0;

        Statement stmt = null;
        Connection connection = null;
        boolean success = false;
        ResultSet rs = null;

        try {
            connection = dataSource.getConnection();

            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            stmt.executeUpdate("INSERT INTO Session (userID, createdTime) "
                    + "VALUES (" + userID + ", '" + timeStamp() + "');");

            connection.commit();

            rs = stmt.executeQuery("SELECT sessionID FROM Session WHERE userID = " + userID + " ORDER BY sessionID  DESC LIMIT 1;");
            while (rs.next()) {
                sessionID = rs.getInt("sessionID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessionID;
    }

    public int GetUserIDForUser(LoginUser user) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        int userID = 0;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT userID FROM Users WHERE username = '" + user.getUsername() + "';");

            while (rs.next()) {
                userID = rs.getInt("userID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userID;
    }

    public int GetUserIDForUser(String username) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        int userID = 0;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT userID FROM Users WHERE username = '" + username + "';");

            while (rs.next()) {
                userID = rs.getInt("userID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userID;
    }

    public ArrayList<String> GetCompaniesForUser(LoginUser user) {
        Statement stmt = null;
        boolean exists = false;
        ResultSet rs = null;
        Connection connection = null;
        ArrayList<String> companies = new ArrayList();

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT companyName FROM User_Company_Mapping, Companies WHERE userID = " + GetUserIDForUser(user) + " AND User_Company_Mapping.companyID=Companies.companyID;");

            while (rs.next()) {
                companies.add(rs.getString("companyName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }

    public void AddNewCompanyForUser(String companyName, String username) {
        Statement stmt = null;
        Connection connection = null;

        AddNewCompany(companyName);

        int companyID = GetCompanyIDForCompany(companyName);
        int userID = GetUserIDForUser(username);

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            stmt.executeUpdate("INSERT INTO User_Company_Mapping (userID_companyID, userID, companyID) "
                    + "VALUES ('" + (userID + "_" + companyID) + "', " + userID + ", " + companyID + ");");

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RemoveCompanyForUser(String companyName, String username) {
        int companyID = GetCompanyIDForCompany(companyName);
        int userID = GetUserIDForUser(username);

        Statement stmt = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            stmt.executeUpdate("DELETE FROM User_Company_Mapping "
                    + "WHERE companyID=" + companyID + " AND userID=" + userID + ";");

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void AddNewCompany(String companyName) {
        Statement stmt = null;
        Connection connection = null;

        if (!DatabaseContainsCompany(companyName)) {
            try {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);

                stmt = connection.createStatement();

                stmt.executeUpdate("INSERT INTO Companies (companyName) "
                        + "VALUES ('" + companyName + "');");

                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int GetCompanyIDForCompany(String companyName) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        int companyID = 0;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT companyID FROM Companies WHERE companyName = '" + companyName + "';");

            while (rs.next()) {
                companyID = rs.getInt("companyID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companyID;
    }

    public boolean DatabaseContainsCompany(String companyName) {
        Statement stmt = null;
        boolean exists = false;
        ResultSet rs = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT * FROM Companies WHERE companyName = '" + companyName + "';");

            if (rs.next()) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    public String GetLastMinedDateForCompany(Company company) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        String date = "";

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT lastDateMined FROM Companies WHERE companyName = '" + company.getCompanyName() + "';");

            while (rs.next()) {
                date = rs.getString("lastDateMined");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return date;
    }

    /*public String[] GetMineDateRangeForCompany(Company company) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        String[] dateRange = new String[2];

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT lastDateMined FROM Companies WHERE companyName = '" + company.getCompanyName() + "';");

            while (rs.next()) {
                dateRange[0] = rs.getString("lastDateMined");
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(dateFormat.parse(dateRange[0]));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.add(Calendar.DATE, 7);
            dateRange[1] = dateFormat.format(calendar.getTime());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return dateRange;
    }*/

    public ArrayList<String> GetAllCompaniesInDatabase() {
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        ArrayList<String> companies = new ArrayList();

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT companyName FROM Companies;");

            while (rs.next()) {
                companies.add(rs.getString("companyName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }

    public void UpdateLastMinedDateForCompany(Company company, String lastUpdateDate) {
        Statement stmt = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            stmt.executeUpdate("UPDATE Companies SET lastDateMined='" + lastUpdateDate + "' WHERE companyName = '" + company.getCompanyName() + "';");

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean IsUserAdmin(LoginUser user) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        int isAdmin = 0;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            stmt = connection.createStatement();

            rs = stmt.executeQuery("SELECT isAdmin FROM Users WHERE username = '" + user.getUsername() + "';");

            while (rs.next()) {
                isAdmin = rs.getInt("isAdmin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (isAdmin == 1)
            return true;

        return false;
    }

    // get current time stamp
    private String timeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }
}
