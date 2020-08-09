package newbank.server;

public class Loan {
    private String loanName;

    private String lenderName;

    private double balance;

    public Loan(String loanName, String lenderName, double balance){
        this.loanName   = loanName;
        this.lenderName = lenderName;
        this.balance    = balance;
    }

    public String getLoanName(){
        return loanName;
    }

    public String toString() {
        return (loanName + ": " + String.format("%.2f", balance) + "; " + lenderName + "\n");
    }

}
