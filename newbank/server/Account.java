package newbank.server;

import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;

public class Account {

    private String accountName;

    private ArrayList<Transaction> transactions = new ArrayList<>();

    /**
     * constructor for a new account
     * @param accountName name of the account
     * @param openingBalance balance when opening
     * @param owner username of the account owner
     */
    public Account(String accountName, double openingBalance, String owner) {
        this.accountName = accountName;
        Transaction t = new Transaction(openingBalance, "opening");
        String currentDate = ""+java.time.LocalDate.now();
        t.setTransParm(accountName,currentDate,owner,"not applicable","not applicable");
        Database.addTransaction(t,1);
    }

    /**
     * constructor for a new account with existing transactions
     * @param accountName name of the account
     * @param transactions transactions so far
     */
    public Account(String accountName, ArrayList<Transaction> transactions){
        this.accountName = accountName;
        this.transactions = transactions;
    }

    /**
     * deposit command, not fully implemented
     * @param amount to deposit
     */
    public void deposit(double amount) {
        transactions.add(new Transaction(amount, "deposit"));

    }

    /**
     * withdrawal command, not fully implemented
     * @param amount
     * @return whether withdrawal was successful
     */
    public boolean withdraw(double amount) {
        if (getBalance() < amount){
            System.out.println("Insufficient funds in account");
            return false;
        } else{
            transactions.add(new Transaction((amount * (-1)), "withdrawal"));
            return true;
        }
    }

    /**
     * calculates the current balance based on database data
     * @return the current balance
     */
    public double getBalance(){
        double sum = 0;
        for (int i=0; i<transactions.size(); i++){
            sum += transactions.get(i).getAmount();
        }
        return sum;
    }

    /**
     * additional DateTime feature, not implemented
     * @return

    public Timestamp getDateTime(){
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        return ts;
    }
    */

    /** Alternative balance attempt with TimeStamp
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
