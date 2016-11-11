package authenticator;

import api.database.sqlite.SQLiteDatabaseConnection;
import ui.web.models.Identity;
import ui.web.models.LoginUser;
import ui.web.models.RegisterUser;

import java.util.ArrayList;

public class Authenticator {
    SQLiteDatabaseConnection sqLiteDatabaseConnection = null;

    public Authenticator() {
        sqLiteDatabaseConnection = new SQLiteDatabaseConnection();
    }

    public boolean RegisterNewUser(RegisterUser user) {

        boolean exist = sqLiteDatabaseConnection.DatabaseContainsUser(user);
        boolean success = false;

        if(!exist){
            System.out.println("User doesn't exist");
            sqLiteDatabaseConnection.AddNewUser(user);
            success = true;
        }else{
            System.out.println("User exists");
        }

        return success;
    }

    public Identity LoginAuthenticator(LoginUser user){
        boolean correct = sqLiteDatabaseConnection.CorrectLoginDetails(user);

        if(correct){
            int sessionID = sqLiteDatabaseConnection.MakeNewSession(user);
            ArrayList<String> companyList = sqLiteDatabaseConnection.GetCompaniesForUser(user);

            return new Identity(""+sessionID, user.getUsername(), companyList, sqLiteDatabaseConnection.IsUserAdmin(user));
        }

        return null;
    }
}
