package server;

import java.util.HashMap;

public class Admin extends User{

    public Admin(String password) {
        this.password = password;
        this.userType = "admin";
        this.accountString = "Error: not a valid customer";
    }
}
