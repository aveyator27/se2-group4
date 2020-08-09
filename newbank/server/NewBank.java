package newbank.server;

//import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
//import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
//import java.io.PrintWriter;

public class NewBank {

    private static final NewBank bank = new NewBank();
    private HashMap<String, User> users;
    private static final String successString = "SUCCESS";
    private static final String failString = "FAIL";
    private static boolean userCustomer = true;
    private static boolean successfulLogin = false;
    private static List errorList = new ArrayList<String>();

    //List with all errors (for password registration purposes)
    public List getErrorList(){
        return errorList;
    }

    private NewBank() {
        users = new HashMap<>();
        addTestData();
    }

    private void addTestData() {
     //test data, no longer in use
    }

    public static NewBank getBank() {
        return bank;
    }

    /**
     * checks a user's login details
     * @param userName user's username
     * @param password password of the user
     * @return the user's userID, otherwise null
     */
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
    }

    // commands from the NewBank customers and admins are processed in this method

    /**
     * commands from the NewBank customers and admins are processed in this method
     * @param customer the user's username
     * @param request the request
     * @return
     */
    public synchronized String processRequest(UserID customer, String request) {
        String command;
        if (request.contains(" ")) {
            command = request.substring(0, request.indexOf(" "));
        } else {
            command = request;
        }

        System.out.println("Command: " + command);
        if ((userCustomer) &&(successfulLogin))
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
                case "SHOWOFFEREDLOANS":
                    return showOfferedLoans(customer);
                case "PRINTSTATEMENT":
                    return printStatement(customer, request);
                default:
                    return failString;
            }
        }  else if ((!userCustomer) &&(successfulLogin))
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

    /**
     * checks if registration would be valid
     * @param username is the potential username
     * @param password the selected password
     * @param passwordRepeat a repeat of the selected password
     * @return whether it is a valid regulation
     */
    public synchronized boolean isValidReg(String username, String password, String passwordRepeat){
        boolean validReg = true;
        errorList.clear();
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
        return Database.showAllCustomers();
    }

    private String showAllAccounts(){
        return Database.showAllAccounts();
    }

    private String showMyAccounts(UserID customer) {
        return Database.showCustomerAccounts(customer.getKey());
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
                "2) To view all accounts enter SHOWACCOUNTS \n" +
                "3) To generate a statement for a customer enter SHOWSTATEMENT followed by their name, e.g. SHOWSTATEMENT Bhagy \n" +

                "\n0) To exit this menu and close down the program, enter EXIT \n"
        );
    }

    /**
     * processes a user's request to send money to another user
     * @param payerID is the username of the user
     * @param request contains the details of the request
     * @return confirmation string or error message
     */
    private String sendMoney(UserID payerID, String request) {
        String[] words = request.split(" ");
        double amount = 0;
        String payee = "";
        String payeeAccount = "";
        if (Database.findCustomerUsername(payerID.getKey()) == null) {
            return("Error: Payer not found");
        }
        if (Database.findCustomerAccount("Main",payerID.getKey()) == null){
            return "Error: Payer's Main Account not found.";
        }

        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {
                if (Database.findCustomerUsername(words[i]) == null ){
                    return "Error: Payee not found.";
                } else {
                    payee = words[i];
                }
                if (Database.findCustomerAccount("Main",words[i]) == null ){
                    return "Error: Payee's Main Account not found.";
                } else {
                    payeeAccount = "Main";
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
            Transaction t1 = new Transaction(-amount,"Paying");
            Transaction t2 = new Transaction(amount, "Paying");
            String currentDate = ""+java.time.LocalDate.now();
            t1.setTransParm("Main",currentDate,payee,payerID.getKey(),"Main");
            t2.setTransParm("Main",currentDate,payerID.getKey(),payee,"Main");
            Database.addTransaction(t1, 1);
            Database.addTransaction(t2, 1);
            return successString;}
        else {
            return "Insufficient Balance";
        }
    }

    /**
     * processes a request for a customer statement
     * @param user username of the customer
     * @param request request details
     * @return string representing statement
     */
    private String showCustomerStatement(UserID user, String request) {
        String userName = null;
        String[] words = request.split(" ");

        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                // ignore the command word
                continue;
            } else if (i == 1) {
                if (!(Database.findCustomerUsername(words[1]) == null)){
                    return Database.showCustomerAccounts(words[1]);
                }
            }
        }
        return failString;
    }

    /**
     * processes request to move funds between a customer's accounts
     * @param customer username of the customer
     * @param request request details
     * @return success or error message
     */
    private String moveFunds(UserID customer, String request) {
        String accountFrom = "";
        String accountTo = "";
        double amount = 0;
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
                }
                System.out.println("Amount: " + amount);
            } else if (i == 2) {
                accountFrom = words[i];
                System.out.println("From: " + words[i]);
            } else if (i == 3) {
                accountTo = words[i];
                System.out.println("To: " + words[i]);
            }
        }
        if (Database.findCustomerAccount(accountFrom, customer.getKey()) == null
                || Database.findCustomerAccount(accountTo, customer.getKey()) == null) {
            errormsg = "Error: Request incomplete.";
            return errormsg;
        }

        if ((Database.EditBalance(accountFrom, customer.getKey(), -amount))) {
            if (Database.EditBalance(accountTo, customer.getKey(), amount)) {
                Transaction t1 = new Transaction(-amount,"Funds moved");
                Transaction t2 = new Transaction(amount, "Funds moved");
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
        return Database.getAccount(customerID.getKey(),accountName);
    }

    /**
     * processes request to print a statement
     * @param customerID the user's username
     * @param request request details
     * @return statement to be printed
     */
    private String printStatement(UserID customerID, String request){
        String[] words = request.split(" ");
        String statement = "";
        Account statementAccount = null;
        for(int i = 0; i<words.length; i++){
            if(i==0){
                statement = Database.showStatement(customerID.getKey(), "Main");
            } else if (i==1){
                statement = Database.showStatement(customerID.getKey(),words[1]);
            }

        }
         return  statement;
    }

    /**
     * sets up a new account
     * @param customerID is the user's username
     * @param request request details
     * @return string indicating whether or not request was successful
     */
    private String newAccount (UserID customerID, String request) {

        String[] words = request.split(" ");

        for (int i = 0; i < words.length; i++){

            if (i==0){
                // ignore the command word
                continue;
            } else if (i==1){

                // Second word from split string is accountName
                String accountName = words[i];
                System.out.println("New Account Name: " + accountName);
                //check that not existing account name
                if (!Database.showCustomerAccounts(customerID.getKey()).contains(accountName)){
                    // customer.addAccount(new Account(accountName, 0));
                    Database.insertAccount(0.00,accountName, customerID.getKey());
                    return successString;

                }
                else {
                    return  failString;
                }

            } else if (i>=2){
                System.out.println("Account name must only contain one word");
                return failString;
            }
        }

        return failString;
    }

    private String showOfferedLoans(UserID customerID){

        Set<String> keySet = users.keySet();

        String output = "";

        for (String userName : keySet){

            if (users.get(userName).userType.equals("customer")){

                Customer customer = (Customer) users.get(userName);

                Account offeredLoanAccount = customer.getAccounts().get("OfferLoan");

                if (offeredLoanAccount==null){
                    continue;
                }

                output = output + "User: " + userName + "; Amount: " + String.format("%.2f", offeredLoanAccount.getBalance());

            }

        }

        if (output==null || output.isEmpty() || output.equals("")){
            return failString;
        } else{
            return output;
        }

    }

}
