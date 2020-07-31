package newbank.server;

import javax.sound.midi.SysexMessage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;



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
        try {
            Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertAccount(double openingBalance, String accountName, String owner) {
        String sql = "INSERT INTO accounts(openingBalance, accountName, owner) values(?, ?, ?)";
        try {
            Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, openingBalance);
            pstmt.setString(2, accountName);
            pstmt.setString(3, owner);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void selectAllCustomers() {
        String sql = "SELECT * FROM customers";

        try {
            Connection conn = Database.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("username") + "\t" +
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void selectAllAccounts() {
        String sql = "SELECT * FROM accounts";

        try {
            Connection conn = Database.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("username") + "\t" +
                        rs.getString("accountType") + "\t" +
                        rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    public static String showCustomerAccounts(String accountName) {
        String sql = "SELECT * FROM accounts WHERE owner = ?";

        try {
            Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,accountName);
            ResultSet rs  = pstmt.executeQuery();
           // conn.close();
            // loop through the result set
            String allAccounts = "";
         /*   while (rs.next()) {
                System.out.println(rs.getDouble("openingBalance") + "|" +
                        rs.getString("accountName") + "|" +
                        rs.getString("owner"));
            }*/
            while (rs.next()){
                allAccounts += rs.getString("accountName") + ": " 
                        + rs.getDouble("openingBalance") + ": " 
                        + rs.getString("owner") + "\n";
            }
            return allAccounts;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

     /*   for(String name: names){
            allAccounts = allAccounts+"\n"+name+":"+"\n" + customers.get(id).accountsToString();
            id++;
        }*/


        return null;

    }

    public static void deleteAccount(String owner, String accountName) {
        String sql = "DELETE FROM accounts WHERE owner = ? AND accountName = ?";

        try {
            Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, owner);
            pstmt.setString(2, accountName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String findCustomerUsername(String username){
        String sql = "SELECT username,password FROM customers WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            pstmt.setString(1,username);
            ResultSet rs  = pstmt.executeQuery();
            //   System.out.println(rs.getString("username") +  "\t" +
            //                rs.getString("password"));

            return rs.getString("username");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String findCustomerPassword(String username){
        String sql = "SELECT password FROM customers WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            pstmt.setString(1,username);
            ResultSet rs  = pstmt.executeQuery();
            //   System.out.println(rs.getString("username") +  "\t" +
            //                rs.getString("password"));

            return rs.getString("password");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}


