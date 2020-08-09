package newbank.server;

public class Admin extends User{
    /**
     * admin user class constructor
     * no longer in use as incompatible with database structure
     * @param password password of the user
     */
    public Admin(String password) {
        this.password = password;
        this.userType = "admin";
        this.accountString = "Error: not a valid customer";
    }
}
