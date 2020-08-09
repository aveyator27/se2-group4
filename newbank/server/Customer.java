package newbank.server;

import java.util.HashMap;
import java.util.Map;

public class Customer extends User{

    /*
    Password field: though "in the wild" it would be bad practice to handle passwords without proper security,
    we consider such a problem out-of-scope for the current exercise.
    */

    private HashMap<String, Account> accounts;

    /**
     * Constructor for a new customer
     * Now legacy as database used
     * @param password is the user's password
     */
    public Customer(String password) {
        this.password = password;
        this.userType = "customer";
        accounts = new HashMap<>();
    }

    /**
     * Turns a user's account details into a string
     * @return string with all account names and balances
     */
    public String accountsToString() {
        String s = "";
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            s += entry.getValue().toString();
        }
        return s;
    }

    public HashMap<String, Account> getAccounts() {
        return accounts;
    }

}
