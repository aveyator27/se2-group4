package newbank.server;

public class User {
    String password;
    String userType;
    String accountString;

    public String getPassword() {
        return password;
    }

    public String accountsToString() { return accountString;}
}
