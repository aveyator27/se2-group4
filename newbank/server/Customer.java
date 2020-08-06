package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Customer extends User{

    /*
    Password field: though "in the wild" it would be bad practice to handle passwords without proper security,
    we consider such a problem out-of-scope for the current exercise.
    */

    private HashMap<String, Account> accounts;

    private ArrayList<Transaction> transactions;

    private int transactionNum;

    public Customer(String password) {
        this.password = password;
        this.userType = "customer";
        accounts = new HashMap<>();
        transactions = new ArrayList<Transaction>();
        transactionNum = 0;
    }

    public String accountsToString() {
        String s = "";
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            s += entry.getValue().toString();
        }
        return s;
    }

    public void addAccount(Account account) {
        accounts.put(account.getAccountName(), account);
    }

    public HashMap<String, Account> getAccounts() {
        return accounts;
    }

    public void addTransaction(Transaction t){
        transactions.add(t);
        Database.addTransaction(t,transactionNum);
        transactionNum++;
    }

}
