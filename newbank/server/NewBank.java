package server;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewBank {
  
    private static final NewBank bank = new NewBank();
    //private HashMap<String, Customer> customers;
    //private HashMap<String, Admin> admins;
    private HashMap<String, User> users;

    private static final String successString = "SUCCESS";
    private static final String failString = "FAIL";

    private NewBank() {
        //customers = new HashMap<>();
        //admins = new HashMap<>();
        users = new HashMap<>();
        addTestData();
    }

    private void addTestData() {
        Customer wayne = new Customer("1234");
        wayne.addAccount(new Account("Main", 134));
        wayne.addAccount(new Account("Savings", 89));
        wayne.addAccount(new Account("testing", 1645));
        users.put("Wayne", wayne);
        Admin admin = new Admin("1234");
        users.put("Admin", admin);
        Admin mel = new Admin("mel");
        users.put("Mel", mel);
    }

    public static NewBank getBank() {
        return bank;
    }

    public synchronized UserID checkLogInDetails(String userName, String password) {
        if ((users.containsKey(userName) && password.equals(users.get(userName).getPassword()))) {
            return new UserID(userName);
        }
        return null;
    }

    /**
     * checks whether a particular username has already been used
     * @param username is the username to check
     * @return whether it has been used already
     */
    private boolean isExistingUser(String username) {
        AtomicBoolean isExisting = new AtomicBoolean(false);
        users.forEach((s,cus) ->{
            if(s.toUpperCase().equals(username.toUpperCase())){
                isExisting.set(true);
            }
        });
        return (isExisting.get());
    }

    // commands from the NewBank customers and admins are processed in this method
    public synchronized String processRequest(UserID customer, String request) {
        String command;
        if (request.contains(" ")) {
            command = request.substring(0, request.indexOf(" "));
        } else {
            command = request;
        }

        System.out.println("Command: " + command);

        if (users.containsKey(customer.getKey())&&users.get(customer.getKey()).userType.equals("customer")) {
            //if customer show customer menu
            switch (command) {
                case "SHOWMYACCOUNTS":
                    return showMyAccounts(customer);
                case "MOVE":
                    return moveFunds(customer, request);
                case "HELP":
                    return showCustomerHelp();
                case "PAY":
                    return sendMoney(customer, request);
                case "NEWACCOUNT":
                    return newAccount(customer,request);
                default:
                    return failString;
            }
        } else if (users.containsKey(customer.getKey())&&users.get(customer.getKey()).userType.equals("admin")){
            //if admin show different menu
            switch (command) {
                case "SHOWCUSTOMERS":
                    return showAllCustomers();
                case "SHOWSTATEMENT":
                   return showCustomerStatement(customer, request);
                case "HELP":
                    return showAdminHelp();
                default:
                    return failString;
            }

        }
        return failString;
    }

    /**
     * creates a new user account
     *
     * @param userName is the user's chosen username
     * @param password is the user's chosen password
     * @return whether account was successfully created
     */
    public synchronized boolean createCustomer(String userName, String password) {
        try {
            if(!isValidReg(userName, password, passwordRepeat)) {
                return false;
            } else {
                //users.put(userName, customer);
                Database.insertCustomer(userName, password);
                Database.insertAccount(0.0, "Main", userName);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String showAllCustomers(){
        ArrayList<Customer> customers = new ArrayList<Customer>();
        ArrayList<String> names = new ArrayList<>();
        String allAccounts = "";
        users.forEach((s,curUser) ->{
            if(curUser.userType.equals("customer")){
                names.add(s);
                customers.add((Customer) curUser);
            }
        });
        int id = 0;
        for (String name : names) {
                allAccounts = allAccounts + "\n" + name + ":" + "\n" + customers.get(id).accountsToString();
                id++;
        }
            return allAccounts;
    }

    private String showAllAccounts(ArrayList<String> names, ArrayList<Customer> customers){
        String allAccounts = "";
        int id = 0;
        for(String name: names){
            allAccounts = allAccounts+"\n"+name+":"+"\n" + customers.get(id).accountsToString();
            id++;
        }
        return allAccounts;
    }

    private String showMyAccounts(UserID customer) {
        return (users.get(customer.getKey())).accountsToString();
    }

    /**
     * aim was to show the balance at a particular time in history
     * take user input of timestamp and use logic in Account.java getBalanceHistory
     * return the sum of transactions before this specified point
     *
     * add to the switch menu for customer
     *
    private String showBalanceHistory(UserID customer, String request){

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(request);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) {
            System.out.println("This input was not in the correct format");
            return failString;
        }

        findCustomerAccount().getBalanceHistory();


    }
     */

    /**
     * creates a help message for a customer
     *
     * @return the string with the help message
     */
    private String showCustomerHelp() {
        return ("Please select one of the following options: \n" +
                "1) To view your accounts enter SHOWMYACCOUNTS \n" +
                "2) To transfer funds between your accounts, enter MOVE followed by the sum and then the two account names and sum. e.g. MOVE 10 Main Savings \n" +
                "3) To pay others customers from your Main account to their Main account, enter PAY followed by that person's name and the amount. e.g. PAY John 100" +
                "4) To open a new account, enter NEWACCOUNT followed by the name of the new account."+
                "5) To show your transactions for an account, enter PRINTSTATEMENT followed by the account name."+
                "\n0) To exit this menu and close down the program, enter EXIT \n"
        );
    }

    /**
     * creates a help message for a admin user
     *
     * @return the string with the help message
     */
    private String showAdminHelp() {
        return ("Please select one of the following options: \n" +
                "1) To view all customers enter SHOWCUSTOMERS \n" +
                "2) To generate a statement for a customer enter SHOWSTATEMENT followed by their name, e.g. SHOWSTATEMENT Bhagy \n" +

                "\n0) To exit this menu and close down the program, enter EXIT \n"
        );
    }

    private String sendMoney(UserID payerID, String request) {
        String[] words = request.split(" ");
        double amount = 0;
        String payee = "";
        String payeeAccount = "";
        if (Database.findCustomerUsername(payerID.getKey()) == null) {
            return("Error: Payer not found");
        }

        Account payeeMain = null;
        if (Database.findCustomerAccount("Main",payerID.getKey()) == null){
            return "Error: Payer's Main Account not found.";
        }

        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {

                // pre database code     payee = (Customer) users.get(words[i]);
                if (Database.findCustomerUsername(words[i]) == null ){
                    return "Error: Payee not found.";
                } else {
                    payee = words[i];
                }
                // pre database code payeeMain = payee.getAccounts().get("Main");
                if (Database.findCustomerAccount("Main",words[i]) == null ){
                    return "Error: Payee's Main Account not found.";
                } else {
                    payeeAccount = "Main";
                }
            } else if (i == 2) {
                amount = Double.valueOf(words[i]);
                if (amount <= 0){
                    System.out.println("Amount must be greater than zero");
                    return failString;
                }
            }
        }
        if (Database.getBalance("Main", payerID.getKey()) >= amount) {
            Database.EditBalance("Main", words[1], amount);
            Database.EditBalance("Main", payerID.getKey(), -amount);
            Transaction t1 = new Transaction(-amount,"Paying");
            Transaction t2 = new Transaction(amount, "Paying");
            String currentDate = ""+java.time.LocalDate.now();
            t1.setTransParm("Main",currentDate,payee,payerID.getKey(),"Main");
            t2.setTransParm("Main",currentDate,payerID.getKey(),payee,"Main");
            Database.addTransaction(t1, 1);
            Database.addTransaction(t2, 1);
            return successString;}


        if (payerMain.withdraw(amount)) {
            payeeMain.deposit(amount);
            return successString;
        } else {
            return failString;
        }
    }

    private String showCustomerStatement(UserID user, String request) {
        String userName = null;
        String[] words = request.split(" ");

        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                // ignore the command word
                continue;
            } else if (i == 1) {
                if (users.containsKey(words[1])) {
                    System.out.println("User identified");
                    return (users.get(words[1]).accountsToString());
                }
            }
        }
            return failString;
        //}
    }


    private String moveFunds(UserID customer, String request) {

        double amount = 0;
        Account from = null;
        Account to = null;

        String[] words = request.split(" ");

        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                // ignore the command word
                continue;
            } else if (i == 1) {
                amount = Double.valueOf(words[i]);
                if (amount <= 0){
                    System.out.println("Amount must be greater than zero");
                    return failString;
                }
                System.out.println("Amount: " + Double.toString(amount));
            } else if (i == 2) {
                from = findCustomerAccount(customer, words[i]);
                System.out.println("From: " + words[i]);
            } else if (i == 3) {
                to = findCustomerAccount(customer, words[i]);
                System.out.println("To: " + words[i]);
            }
        }

        if (from == null || to == null) {
            System.out.println("Error: Request incomplete.");
            return failString;
        }

        if (from.withdraw(amount)) {
            to.deposit(amount);
            return successString;
        } else {
            return failString;
        }
//
        if ((Database.EditBalance(accountFrom, customer.getKey(), -amount))) {
            if (Database.EditBalance(accountTo, customer.getKey(), amount)) {
                Transaction t1 = new Transaction(-amount,"Funds moved");
                //    t1.setTransParm();
                Transaction t2 = new Transaction(amount, "Funds moved");
                //    Account a1 = findCustomerAccount(customer, accountFrom);
                //     Account a2 = findCustomerAccount(customer, accountTo);
                //       a1.addTransaction(t1);
                //        a2.addTransaction(t2);
                String currentDate = ""+java.time.LocalDate.now();
                t1.setTransParm(accountFrom,currentDate,customer.getKey(),customer.getKey(),accountTo);
                t2.setTransParm(accountTo,currentDate,customer.getKey(),customer.getKey(),accountFrom);
                Database.addTransaction(t1, 1);
                Database.addTransaction(t2, 1);
                return successString;
            } else {
                //if the second transfer cannot be completed, add balance back
                if(Database.EditBalance(accountFrom, customer.getKey(), amount)){
                    return failString;
                } else {
                    return "There has been a critical issue and money was withdrawn but not added.\n Please call customer support immediately with error code 01.";
                }
            }

        }
        return failString;
    }

    private Account findCustomerAccount(UserID customerID, String accountName) {
      /*  Customer customer = (Customer) users.get(customerID.getKey());
        if (customer==null){
            return null;
        } else {
            return customer.getAccounts().get(accountName);
        }*/
        return Database.getAccount(customerID.getKey(),accountName);

    }

    private String printStatement(UserID customerID, String request){
        String[] words = request.split(" ");
        String statement = "";
        Account statementAccount = null;
        for(int i = 0; i<words.length; i++){
            if(i==0){
                statement = Database.showStatement(customerID.getKey(), "Main");
                // I need to write a database method for this
            } else if (i==1){
                statement = Database.showStatement(customerID.getKey(),words[1]);
                // database method for this
            }

        }
         return  statement;
     /*   if (statementAccount == null){
            return "Error. Account could not be found";
        } else {
            ArrayList<Transaction> transactions = statementAccount.getTransactions();
            int i = 1;
            for(Transaction t: transactions){
                statement = statement+"Transaction: "+i+"\n"+"Date: "+t.getDate()+" Amount: "+t.getAmount()+" Ref:"+t.getRef()+" From/To: "+t.getCustomer()+" Account:"+t.getAccount()+" \n";
            i++;
            }
        }
        return statement;
        //  return Database.showStatement(customerID.getKey());
        return "error";*/
    }

    private String newAccount (UserID customerID, String request) {

    	for (int i = 0; i < words.length; i++){

    	    if (i==0){
    	        // ignore the command word
                continue;
    	    } else if (i==1){

                // Second word from split string is accountName
    	        String accountName = words[i];

    	        System.out.println("New Account Name: " + accountName);

    	        Customer customer = (Customer) users.get(customerID.getKey());
    				
    	        if (customer.getAccounts().get(accountName)==null){
    	            customer.addAccount(new Account(accountName, 0));

    	            return successString;

    	        } else {
    	            return failString;
    	        }

    	    } else if (i>=2){
    	        System.out.println("Account name must only contain one word");
    	        return failString;
    	    }
    	}

    	return failString;
    }
}
