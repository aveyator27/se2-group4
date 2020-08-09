package server;

import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;

public class Account {

    private String accountName;

    //private double openingBalance;

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public Account(String accountName, double openingBalance, String owner) {
        this.accountName = accountName;
        //this.openingBalance = openingBalance;
        Transaction t = new Transaction(openingBalance, "opening");
        String currentDate = ""+java.time.LocalDate.now();
        t.setTransParm(accountName,currentDate,owner,"not applicable","not applicable");
        Database.addTransaction(t,1);
    }

    public Account(String accountName, ArrayList<Transaction> transactions){
        this.accountName = accountName;
        this.transactions = transactions;
    }

    public void deposit(double amount) {

        //openingBalance = openingBalance + amount;
        transactions.add(new Transaction(amount, "deposit", getDateTime()));

    }

    public boolean withdraw(double amount) {
        if (getBalance() < amount){
            System.out.println("Insufficient funds in account");
            return false;
        } else{
            transactions.add(new Transaction((amount * (-1)), "withdrawal", getDateTime()));
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
    public double getBalance(){
        double sum = 0;
        for (int i=0; i<transactions.size(); i++){
            sum += transactions.get(i).getAmount();
        }
        return sum;
    }

    public Timestamp getDateTime(){
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        return ts;
    }

    /**
     * call from NewBank showBalanceHistory
    public double getBalanceHistory(Timestamp dateTime){
        double sum = 0;
        //Timestamp markerTime = GET THIS FROM USER;
        for (int i=0; i< transactions.size(); i++){
            if (dateTime.before(markerTime)){
                sum += transactions.get(i).getAmount();
            }
            else{
                continue;
            }
        }
        return sum;
    }*/

    public String getAccountName() {
        return accountName;
    }

    public String toString() {
        return (accountName + ": " + String.format("%.2f", getBalance()) + "\n");
    }

    public ArrayList<Transaction> getTransactions(){
        return transactions;
    }
    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
    }



}
