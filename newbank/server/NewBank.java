package newbank.server;

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

        Customer wayne = new Customer("1234");
        wayne.addAccount(new Account("Main", 134));
        wayne.addAccount(new Account("Savings", 89));
        wayne.addAccount(new Account("testing", 1645));
        users.put("Wayne", wayne);

        Admin admin = new Admin("1234");
        users.put("Admin", admin);
        Admin mel = new Admin("mel");
        users.put("Mel", mel);
       // Database.insertAdmin("Sam","1234");
    }

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

    /**
     * creates a new user account
     *
     * @param userName is the user's chosen username
     * @param password is the user's chosen password
     * @return whether account was successfully created
     */
    public synchronized boolean createCustomer(String userName, String password, String passwordRepeat) {
        try {
            boolean validPassword = true;
            List<String> errorList = new ArrayList<String>();
            Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
            Pattern lowerCasePatten = Pattern.compile("[a-z ]");
            Pattern digitCasePatten = Pattern.compile("[0-9 ]");
            if (!password.equals(passwordRepeat)) {
                errorList.add("Passwords do not match.");
                validPassword = false;
            }
            if (password.length() < 8) {
                errorList.add("Password length must contain at least eight characters.");
                validPassword = false;

            }
            if (!specialCharPatten.matcher(password).find()) {
                errorList.add("The password must contain at least one special character.");
                validPassword = false;
            }
            if (!UpperCasePatten.matcher(password).find()) {
                errorList.add("The password must contain at least one upper case character.");
                validPassword = false;
            }
            if (!lowerCasePatten.matcher(password).find()) {
                errorList.add("The password must contain at least one lower case character.");
                validPassword = false;
            }
            if (!digitCasePatten.matcher(password).find()) {
                errorList.add("The password must contain at least one digit 0-9.");
                validPassword = false;
            }

            if (validPassword) {
            //    Customer customer = new Customer(password);
            //    customer.addAccount(new Account("Main", 0.0));
                if (isExistingUser(userName)) {
                    return false;
                } else {
                    Database.insertCustomer(userName, password);
                    Database.insertAccount(0.0, "Main", userName);
                    //users.put(userName, customer);
                    return true;
                }
            }
            System.out.println(errorList);

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
        String[] words = request.split(" ");
        double amount = 0;
        Customer payee = null;
        Customer payer = (Customer) users.get(payerID.getKey());
        if (payer==null){
            System.out.println("Error: Payer not found");
            return failString;
        }

        Account payeeMain = null;
        Account payerMain = payer.getAccounts().get("Main");

        if (payerMain == null ){
            System.out.println("Error: Payer's Main Account not found.");
            return failString;
        }

        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {
                payee = (Customer) users.get(words[i]);
                if (payee == null ){
                    System.out.println("Error: Payee not found.");
                    return failString;
                }
                payeeMain = payee.getAccounts().get("Main");
                if (payeeMain == null ){
                    System.out.println("Error: Payee's Main Account not found.");
                    return failString;
                }
            } else if (i == 2) {
                amount = Double.valueOf(words[i]);
                if (amount <= 0){
                    System.out.println("Amount must be greater than zero");
                    return failString;
                }
            }
        }

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
