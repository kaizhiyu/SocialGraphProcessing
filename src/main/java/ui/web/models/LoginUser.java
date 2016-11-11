package ui.web.models;

/**
 * Created by kryli on 2016/10/24.
 */
public class LoginUser {

    private String username;
    private String password;

    public String getHashedPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = Integer.toString(password.hashCode());
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
