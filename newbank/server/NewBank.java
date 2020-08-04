package newbank.server;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.io.PrintWriter;

public class NewBank {

    private static final NewBank bank = new NewBank();
    //private HashMap<String, Customer> customers;
    //private HashMap<String, Admin> admins;
    private HashMap<String, User> users;

    private static final String successString = "SUCCESS";
    private static final String failString = "FAIL";
    private static boolean userCustomer = true;
    private static boolean successfulLogin = false;
    private static List errorList = new ArrayList<String>();

    public List getErrorList(){
        return errorList;
    }

    private NewBank() {
        //customers = new HashMap<>();
        //admins = new HashMap<>();
        users = new HashMap<>();
        addTestData();
    }

    private void addTestData() {
        Customer bhagy = new Customer("1234");
        bhagy.addAccount(new Account("Main", 1000.0));
        users.put("Bhagy", bhagy);
        //    Database.insertCustomer("Bhagy", "1234");
        //   Database.showCustomerAccounts("Christina");


        Customer christina = new Customer("Tina01");
        christina.addAccount(new Account("Main", 800.0));
        christina.addAccount(new Account("Savings", 1500.0));
        //   Database.insertAccount(0.00, "Main", "Christina");
        //   Database.insertAccount(1500.00, "Savings", "Christina");
        users.put("Christina", christina);
        //    Database.insertCustomer("Christina", "Tina01");
        //   Database.findCustomerUsername("Christina");

        Customer john = new Customer("JohnDoe");
        john.addAccount(new Account("Main", 800.0));
        john.addAccount(new Account("Checking", 250.0));
        users.put("John", john);
        //     Database.insertAccount(0.00, "Main", "JohnDoe");
        //   Database.deleteAccount("JohnDoe", "Main");
        Customer marc = new Customer("password");
        marc.addAccount(new Account("Main", 134));
        marc.addAccount(new Account("Savings", 89));
        marc.addAccount(new Account("BottlecapsStash", 1645));
        users.put("Marc", marc);
        //   Database.insertAccount(0.00, "Main","Marc");
        //   Database.deleteAccount("Marc","Main");
        //    Database.EditBalance("Main", "1234",100.00);
        //    Database.EditBalance("Main", "123",100.00);

        Customer wayne = new Customer("1234");
        wayne.addAccount(new Account("Main", 134));
        wayne.addAccount(new Account("Savings", 89));
        wayne.addAccount(new Account("testing", 1645));
        users.put("Wayne", wayne);

        Admin admin = new Admin("1234");
        users.put("Admin", admin);
        Admin mel = new Admin("mel");
        users.put("Mel", mel);
        //  ArrayList<String> m = Database.showCustomerAccountsFormat("123");
        //     for (String item : m){
        //       System.out.println(item);
    }



    // Database.insertAdmin("Sam","1234");


    public static NewBank getBank() {
        return bank;
    }

    public synchronized UserID checkLogInDetails(String userName, String password) {
        if (Database.findCustomerUsername(userName) != null) {
            System.out.println(Database.findCustomerUsername(userName));
            if (Database.findCustomerPassword(userName).equals(password)) {
                successfulLogin = true;
                return new UserID(userName);
            }
        }
        if (Database.findAdminUsername(userName) != null) {
            System.out.println(Database.findCustomerUsername(userName));
            if (Database.findAdminPasword(userName).equals(password)) {
                successfulLogin = true;
                userCustomer = false;
                return new UserID(userName);
            }
  /*    if ((users.containsKey(userName) && password.equals(users.get(userName).getPassword()))) {
            return new UserID(userName);
        }
        return null;*/

        }
        return null;
    }

    /**
     * checks whether a particular username has already been used
     * @param username is the username to check
     * @return whether it has been used already
     */
    private boolean isExistingUser(String username) {
        Boolean isExisting = false;
        if (Database.findCustomerUsername(username)!=null){
            isExisting = true;
        }
        return isExisting;
        /* pre Database code:
         AtomicBoolean isExisting = new AtomicBoolean(false);
        users.forEach((s,cus) ->{
            if(s.toUpperCase().equals(username.toUpperCase())){
                isExisting.set(true);
            }
        });
        return (isExisting.get());*/
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
        if ((userCustomer) &&(successfulLogin))
        //   if (users.containsKey(customer.getKey())&&users.get(customer.getKey()).userType.equals("customer"))
        {
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
        }  else if ((!userCustomer) &&(successfulLogin))
        // else if (users.containsKey(customer.getKey())&&users.get(customer.getKey()).userType.equals("admin"))
        {
            //if admin show different menu
            switch (command) {
                case "SHOWCUSTOMERS":
                    return showAllCustomers();
                case "SHOWSTATEMENT":
                    return showCustomerStatement(customer, request);
                case "SHOWACCOUNTS":
                    return showAllAccounts();
                case "HELP":
                    return showAdminHelp();
                default:
                    return failString;
            }

        }
        return failString;
    }

    public synchronized boolean isValidReg(String username, String password, String passwordRepeat){
        boolean validReg = true;
        errorList.clear();
        //List<String> errorList = new ArrayList<String>();
        Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");
        if (isExistingUser(username)){
            errorList.add("User already exists.");
            validReg = false;
        }
        if (!password.equals(passwordRepeat)) {
            errorList.add("Passwords do not match.");
            validReg = false;
        }
        if (password.length() < 8) {
            errorList.add("Password length must contain at least eight characters.");
            validReg = false;

        }
        if (!specialCharPatten.matcher(password).find()) {
            errorList.add("The password must contain at least one special character.");
            validReg = false;
        }
        if (!UpperCasePatten.matcher(password).find()) {
            errorList.add("The password must contain at least one upper case character.");
            validReg = false;
        }
        if (!lowerCasePatten.matcher(password).find()) {
            errorList.add("The password must contain at least one lower case character.");
            validReg = false;
        }
        if (!digitCasePatten.matcher(password).find()) {
            errorList.add("The password must contain at least one digit 0-9.");
            validReg = false;
        }
        return validReg;
    }

    /**
     * creates a new user account
     *
     * @param userName is the user's chosen username
     * @param password is the user's chosen password
     * @return whether account was successfully created
     */
    public synchronized boolean createCustomer(String userName, String password, String passwordRepeat) {
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
        return false;
    }

    private String showAllCustomers(){

     /*  ArrayList<Customer> customers = new ArrayList<Customer>();
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
        */
        return Database.showAllCustomers();
    }

    private String showAllAccounts(){ //ArrayList<String> names, ArrayList<Customer> customers
/*
        String allAccounts = "";
        int id = 0;
        for(String name: names){
            allAccounts = allAccounts+"\n"+name+":"+"\n" + customers.get(id).accountsToString();
            id++;
        }
        return allAccounts;*/
        return Database.showAllAccounts();

    }

    private String showMyAccounts(UserID customer) {
        return Database.showCustomerAccounts(customer.getKey());
        // pre database code
        //       System.out.println(users.get(customer.getKey()).accountString);
        //       return (users.get(customer.getKey())).accountsToString();
    }

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
                "1) To view all accounts enter SHOWACCOUNTS \n" +
                "2) To generate a statement for a customer enter SHOWSTATEMENT followed by their name, e.g. SHOWSTATEMENT Bhagy \n" +

                "\n0) To exit this menu and close down the program, enter EXIT \n"
        );
    }

    private String sendMoney(UserID payerID, String request) {
        Boolean balance = true;
        String[] words = request.split(" ");
        double amount = 0;
        Customer payee = null;
        // pre database code Customer payer = (Customer) users.get(payerID.getKey());

        // pre database code  if (payer==null){
        //      System.out.println("Error: Payer not found");
        //       return failString;
        //   }
        if (Database.findCustomerUsername(payerID.getKey()) == null) {
            return("Error: Payer not found");
        }
        Account payeeMain = null;
        // pre database code   Account payerMain = payer.getAccounts().get("Main");
        if (Database.findCustomerAccount("Main",payerID.getKey()) == null){
            return "Error: Payer's Main Account not found.";
            //return failString;
        }
   /* pre database code    if (payerMain == null ){
            System.out.println("Error: Payer's Main Account not found.");
            return failString;
        }*/

        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {

                // pre database code     payee = (Customer) users.get(words[i]);
                if (Database.findCustomerUsername(words[i]) == null ){
                    return "Error: Payee not found.";
                }
                // pre database code payeeMain = payee.getAccounts().get("Main");
                if (Database.findCustomerAccount("Main",words[i]) == null ){
                    return "Error: Payee's Main Account not found.";
                }
            } else if (i == 2) {
                amount = Double.valueOf(words[i]);
                if (amount <= 0){
                    return "Amount must be greater than zero";
                }
            }
        }
        if (Database.getBalance("Main", payerID.getKey()) >= amount) {
            Database.EditBalance("Main", words[1], amount);
            Database.EditBalance("Main", payerID.getKey(), -amount);
            return successString;}

        // pre database code  if (payerMain.withdraw(amount)) {
        //      payeeMain.deposit(amount);
        //      return successString;
        else {
            return "Insufficient Balance";
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
                //    if (users.containsKey(words[1])) {
                if (!(Database.findCustomerUsername(words[1]) == null)){
                    return Database.showCustomerAccounts(words[1]);
                    //     return (users.get(words[1]).accountsToString());
                }
            }
        }
        return failString;
        //}
    }


    private String moveFunds(UserID customer, String request) {
        String accountFrom = "";
        String accountTo = "";
        double amount = 0;
        Account from = null;
        Account to = null;
        String errormsg = "";

        String[] words = request.split(" ");

        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                // ignore the command word
                continue;
            } else if (i == 1) {
                amount = Double.valueOf(words[i]);
                if (amount <= 0) {
                    errormsg = "Amount must be greater than zero";
                    return errormsg;
                    //System.out.println("Amount must be greater than zero");
                    //return failString;
                }
                System.out.println("Amount: " + amount);
            } else if (i == 2) {
                //     from = findCustomerAccount(customer, words[i]);
                accountFrom = words[i];
                System.out.println("From: " + words[i]);
            } else if (i == 3) {
                //      to = findCustomerAccount(customer, words[i]);
                accountTo = words[i];
                System.out.println("To: " + words[i]);
            }
        }

   /*     if (from == null || to == null) {
            System.out.println("Error: Request incomplete.");
            return failString;
        }*/
        if (Database.findCustomerAccount(accountFrom, customer.getKey()) == null
                || Database.findCustomerAccount(accountTo, customer.getKey()) == null) {
            errormsg = "Error: Request incomplete.";
            //System.out.println("Error: Request incomplete.");
            //return failString;
            return errormsg;
        }

       /*  if (from.withdraw(amount)) {
            to.deposit(amount);
            return successString;
        } else {
            return failString;
        }
*/
        if ((Database.EditBalance(accountFrom, customer.getKey(), -amount))) {
            if (Database.EditBalance(accountTo, customer.getKey(), amount)) {
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

        Customer customer = (Customer) users.get(customerID.getKey());

        if (customer==null){
            return null;
        } else {
            return customer.getAccounts().get(accountName);
        }

    }

    private String newAccount (UserID customerID, String request) {

        String[] words = request.split(" ");

        for (int i = 0; i < words.length; i++){

            if (i==0){
                // ignore the command word
                continue;
            } else if (i==1){

                // Second word from split string is accountName
                String accountName = words[i];
                // Need to check for non conflicting
                System.out.println("New Account Name: " + accountName);
                //   Customer customer = (Customer) users.get(customerID.getKey());
                //check that not existing account name
                if (!Database.showCustomerAccounts(customerID.getKey()).contains(accountName)){
                    // customer.addAccount(new Account(accountName, 0));
                    Database.insertAccount(0.00,accountName, customerID.getKey());
                    return successString;

                }
                else {
                    return  failString;
                }
                /*if (customer.getAccounts().get(accountName)==null){   pre database code
                    customer.addAccount(new Account(accountName, 0));
                    return successString;
                } else {
                    return failString;
                }*/

            } else if (i>=2){
                System.out.println("Account name must only contain one word");
                return failString;
            }
        }

        return failString;
    }
}
