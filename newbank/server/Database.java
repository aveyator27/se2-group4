package newbank.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Database {

    /**
     * Connects to the database
     * @return the connection
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // database parameters
            String url = "jdbc:sqlite:db/bankDatabase.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                return conn;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * insert a new customer into the database
     * @param username the new customers username
     * @param password the new customers password
     */
    public static void insertCustomer(String username, String password) {
        String sql = "INSERT INTO customers(username, password) values(?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * adds a admin user to the database
     * not yet in use
     * @param username is the admin's username
     * @param password is the admin's password
     */
    public static void insertAdmin(String username, String password) {
        String sql = "INSERT INTO admin(username, password, userType) values(?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, "admin");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * inserts a new account into the database
     * @param openingBalance the balance for the account
     * @param accountName the name of the new account
     * @param owner the username of the owner of the account
     */
    public static void insertAccount(double openingBalance, String accountName, String owner) {
        String sql = "INSERT INTO accounts(openingBalance, accountName, owner) values(?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, openingBalance);
            pstmt.setString(2, accountName);
            pstmt.setString(3, owner);
            pstmt.executeUpdate();
            Account a = new Account(accountName,0.00, owner);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * creates a String representing a list of all customers in the system
     * @return the list of customers' usernames
     */
    public static String showAllCustomers() {
        String sql = "SELECT * FROM customers";
        String customers = "";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            // loop through the result set
            while (rs.next()) {
                customers += "username :" + (rs.getString("username")) + "\n";
            }
            return customers;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * creates a string list of all customers' accounts in the system
     * @return a string showing all customers and their relative accounts
     */
    public static String showAllAccounts() {
        String sql = "SELECT * FROM accounts";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            // loop through the result set
            String accounts = "";
            while (rs.next()) {
                accounts += "Balance :" + (rs.getString("openingBalance") + ": " +
                        "Account Name" + ": " + rs.getString("accountName")) + ": "
                        + "Owner" + ": " + rs.getString("owner") + "\n";
            }
            return accounts;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;


    }

    /**
     * returns all accounts of a particular customer as a string list
     * @param owner username of the customer
     * @return string representing list of accounts
     */
    public static String showCustomerAccounts(String owner) {
        String sql = "SELECT * FROM accounts WHERE owner = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, owner);
            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            String allAccounts = "";
            while (rs.next()) {
                allAccounts += rs.getString("accountName") + ": "
                        + rs.getDouble("openingBalance") + ": "
                        + rs.getString("owner") + "\n";
            }
            return allAccounts;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * deletes an account from the database, currently unused
     */
    public static void deleteAccount(String owner, String accountName) {
        String sql = "DELETE FROM accounts WHERE owner = ? AND accountName = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, owner);
            pstmt.setString(2, accountName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * returns a customer based on their username
     * @param username
     * @return username to confirm they exist
     */
    public static String findCustomerUsername(String username) {
        String sql = "SELECT username FROM customers WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.getString("username");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * confirms whether a customer password is correct
     * @param username is the username of the user being checked
     * @return password if correct
     */
    public static String findCustomerPassword(String username) {
        String sql = "SELECT password FROM customers WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.getString("password");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * returns admin based on their username
     * @param username
     * @return username to confirm they exist
     */
    public static String findAdminUsername(String username) {
        String sql = "SELECT username FROM admin WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            //   System.out.println(rs.getString("username") +  "\t" +
            //                rs.getString("password"));

            return rs.getString("username");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     /**
     * confirms whether a admin password is correct
     * @param username is the username of the user being checked
     * @return password if correct
     */
    public static String findAdminPasword(String username) {
        String sql = "SELECT password FROM admin WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.getString("password");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * edit the balance of an account in the database
     * @param accountName name of the account
     * @param owner username of the owner
     * @param amount new balance
     * @return
     */
    public static boolean EditBalance(String accountName, String owner, Double amount) {
        double newBalance = Database.getBalance(accountName, owner) + amount;
        if (newBalance < 0){
            return false;
        }
        String sql = "UPDATE accounts SET openingBalance = ? WHERE owner = ? AND accountName = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, owner);
            pstmt.setString(3, accountName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * moves money from one account to another in
     * @param accountNameFrom account money is deducted from
     * @param accountNameTo account money is added to
     * @param owner owner of the accounts
     * @param amount amount of money moved
     * @return
     */
    public static boolean EditBalanceMove(String accountNameFrom,String accountNameTo, String owner, Double amount) {
        double newBalance = 0;
        boolean balanceCheck = Database.getBalance(accountNameFrom, owner) > amount;
        if (!balanceCheck){
            return false;
        } else {
            newBalance = Database.getBalance(accountNameTo, owner) + amount;
        }
        String sql = "UPDATE accounts SET openingBalance = ? WHERE owner = ? AND accountName = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, owner);
            pstmt.setString(3, accountNameTo);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * gets the current balance of an account from the database
     * @param accountName name of the account
     * @param owner owner of the account
     * @return
     */
    public static Double getBalance(String accountName, String owner) {
        String sql = "SELECT openingBalance FROM accounts WHERE accountName = ? AND owner = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountName);
            pstmt.setString(2, owner);
            ResultSet rs = pstmt.executeQuery();
            return rs.getDouble("openingBalance");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1.1;
    }

    /**
     * finds a customer account
     * @param accountName name of the account
     * @param owner owner of the account
     * @return name of the account if it exists
     */
    public static String findCustomerAccount(String accountName, String owner) {
        String sql = "SELECT accountName = ? FROM accounts WHERE owner = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountName);
            pstmt.setString(2, owner);
            ResultSet rs = pstmt.executeQuery();
            return rs.toString();

        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
        return null;
    }

    /**
     * add a new transaction to the database
     * @param t is the transaction
     * @param index the current index
     */
    public static void addTransaction(Transaction t, int index){
        String customername = t.getCustomer();
        String accountname = t.getAccount();
        Double amount = t.getAmount();
        String ref = t.getRef();
        String account = t.getRecipientAccount();
        String date = t.getDate();
        String customer = t.getRecipient();
        String sql = "INSERT INTO transactions(customerName, accountName, t_index, t_amount, t_Ref, t_Customer, t_Account, t_Date) values(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customername);
            pstmt.setString(2, accountname);
            pstmt.setInt(3, index);
            pstmt.setDouble(4, amount);
            pstmt.setString(5,ref);
            pstmt.setString(6,customer);
            pstmt.setString(7,account);
            pstmt.setString(8,date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage()); }
    }

    /**
     * confirms an account exists
     * @param customerName the customers name
     * @param accountName the account name
     * @return account name if it exists, otherwise null
     */
    public static Account getAccount(String customerName, String accountName){
        String sql = "SELECT * FROM transactions WHERE CustomerName = ? AND AccountName = ?";
        try {
            Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customerName);
            pstmt.setString(2, accountName);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Transaction> transactions = new ArrayList<>();
            if (rs==null){
                return null;
            }
            while (rs.next()) {
                Transaction t = new Transaction(rs.getDouble("Double"), rs.getString("t_Ref"));
                transactions.add(t);
            }
            return new Account(accountName, transactions);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * creates a string representing the customer's statement
     * @param customer is the username of the customer
     * @param account the name of the account
     * @return string that represents the statement
     */
    public static String showStatement(String customer, String account) {
        String sql = "SELECT * FROM transactions WHERE customerName = ? AND accountName = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer);
            pstmt.setString(2,account);
            ResultSet rs = pstmt.executeQuery();
            String statement = "";
            while (rs.next()) {
                statement += rs.getString("customerName") + ": "
                        + rs.getString("accountName") + ": "
                        + rs.getString("t_amount") + ": "
                        + rs.getString("t_Ref") + ": " +
                        rs.getString("t_Customer") + ": " +
                        rs.getString("t_Account") + ": " +
                        rs.getString("t_Date")
                        + "\n";

            }
            return statement;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

