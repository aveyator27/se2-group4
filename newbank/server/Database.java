package newbank.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Database {


    public static Connection connect() {
        Connection conn = null;
        try {                // "jdbc:sqlite:/Users/samueljewell/NewBanklatest11/db/bankDatabase.db"
            // db parameters
            String url = "jdbc:sqlite:db/bankDatabase.db";      // jdbc:sqlite:bankDatabase.db  // jdbc:sqlite:db/bankDatabase.db
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

    public static void insertAccount(double openingBalance, String accountName, String owner) {
        String sql = "INSERT INTO accounts(openingBalance, accountName, owner) values(?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, openingBalance);
            pstmt.setString(2, accountName);
            pstmt.setString(3, owner);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String showAllCustomers() {
        String sql = "SELECT * FROM customers";

        try (Connection conn = Database.connect();
             Statement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            String customers = "";
            while (rs.next()) {
                customers += "username :" + (rs.getString("username") + ": " +
                        "password" + ": " + rs.getString("password")) + "\n";
            }
            return customers;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static String showAllAccounts() {
        String sql = "SELECT * FROM accounts";

        try (Connection conn = Database.connect();
             Statement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            String accounts = "";
            while (rs.next()) {
                accounts += "openingBalance :" + (rs.getString("openingBalance") + ": " +
                        "accountName" + ": " + rs.getString("accountName")) + ": "
                        + "owner" + ": " + rs.getString("owner") + "\n";
            }
            return accounts;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;


    }

    public static String showCustomerAccounts(String owner) {
        String sql = "SELECT * FROM accounts WHERE owner = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, owner);
            ResultSet rs = pstmt.executeQuery();
            // conn.close();
            // loop through the result set
            String allAccounts = "";
         /*   while (rs.next()) {
                System.out.println(rs.getDouble("openingBalance") + "|" +
                        rs.getString("accountName") + "|" +
                        rs.getString("owner"));
            }*/
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

    public static ArrayList<String> showCustomerAccountsFormat(String owner) {
        String sql = "SELECT * FROM accounts WHERE owner = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, owner);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<String> allAccounts = new ArrayList();
            while (rs.next()) {
                allAccounts.add(rs.getString("accountName"));
            }
            return allAccounts;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;

    }

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

    public static String findCustomerUsername(String username) {
        String sql = "SELECT username FROM customers WHERE username = ?";

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

    public static String findAdminPasword(String username) {
        String sql = "SELECT password FROM admin WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            //   System.out.println(rs.getString("username") +  "\t" +
            //                rs.getString("password"));

            return rs.getString("password");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

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

    public static void addTransaction(Transaction t, int index){
        String customerName = t.getCustomer();
        String accountName = t.getAccount();
        Double amount = t.getAmount();
        String ref = t.getRef();
        String account = t.getAccount();
        String date = t.getDate();
        String recipient = t.getRecipient();
        String sql = "INSERT INTO transactions(customerName, accountName, index, t_amount, t_Ref, t_Customer, t_Account, t_Date) values(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerName);
            pstmt.setString(2, accountName);
            pstmt.setInt(3, index);
            pstmt.setDouble(4, amount);
            pstmt.setString(5,ref);
            pstmt.setString(6,account);
            pstmt.setString(7,date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage()); }
    }
}
