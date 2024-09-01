package BlackJack;
import java.io.Serializable;

public class UserAuthentication implements Serializable {
    private String username;
    private String password;
    private UserAuthenticationType type;

    public UserAuthentication(String username, String password, UserAuthenticationType type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public boolean authenticate(UserAuthentication x) {
        return x.username.equals(this.username) && x.password.equals(this.password);
    }

    // getters and setters for username and password
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(UserAuthenticationType type) {
        this.type = type;
    }

    public UserAuthenticationType getType() {
        return this.type;
    }
}