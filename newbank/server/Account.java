package newbank.server;

public class Account {

    private String accountName;

    private double openingBalance;

    public Account(String accountName, double openingBalance) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
    }

    public void deposit(double amount) {
        openingBalance = openingBalance + amount;
    }

    public boolean withdraw(double amount) {
        if (openingBalance < amount) {
            System.out.println("Insufficient funds in account");
            return false;
        } else {
            openingBalance = openingBalance - amount;
            return true;
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public String toString() {
        return (accountName + ": " + String.format("%.2f", openingBalance) + "\n");
    }

}
