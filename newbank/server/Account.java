package newbank.server;

import java.util.ArrayList;

public class Account {

    private String accountName;

    //private double openingBalance;

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public Account(String accountName, double openingBalance) {
        this.accountName = accountName;
        //this.openingBalance = openingBalance;
        transactions.add(new Transaction(openingBalance, "opening"));
    }

    public void deposit(double amount) {

        //openingBalance = openingBalance + amount;
        transactions.add(new Transaction(amount, "deposit"));

    }

    public boolean withdraw(double amount) {
        if (getBalance() < amount){
            System.out.println("Insufficient funds in account");
            return false;
        } else{
            transactions.add(new Transaction((amount * (-1)), "withdrawal"));
            return true;
        }
        /*if (openingBalance < amount) {
            System.out.println("Insufficient funds in account");
            return false;
        } else {
            openingBalance = openingBalance - amount;
            return true;
        }*/
    }

    // get balance by summing transaction array element
    private double getBalance(){
        double sum = 0;
        for (int i=0; i<transactions.size(); i++){
            sum += transactions.get(i).getAmount();
        }
        return sum;
    }

    public String getAccountName() {
        return accountName;
    }

    public String toString() {
        return (accountName + ": " + String.format("%.2f", getBalance()) + "\n");
    }

    public void addTransaction(Transaction t){
        transactions.add(t);
        Database.addTransaction(t, transactions.size());
    }

    public ArrayList<Transaction> getTransactions(){
        return transactions;
    }

}
