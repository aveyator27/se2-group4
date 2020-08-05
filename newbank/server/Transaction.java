package newbank.server;

public class Transaction {
    double t_Amount;
    String t_Ref;
    String t_Customer;
    String t_Account;
    String t_Date;

    public Transaction(double amount, String reference){
        t_Amount=amount;
        t_Ref=reference;
    }

    public String getDate(){
        return t_Date;
    }
    public void setDate(String date){
        this.t_Date = date;
    }

    public String getCustomer(){
        return t_Customer;
    }
    public void setCustomer(String customer){
        this.t_Customer = customer;
    }

    public String getAccount(){
        return t_Account;
    }
    public void setAccount(String account){
        this.t_Account = account;
    }

    public double getAmount(){
        return t_Amount;
    }
    public void setAmount(double amount){
        this.t_Amount = amount;
    }

    public String getRef(){
        return t_Ref;
    }
    public void setRef(String ref){ this.t_Ref = ref; }

}
