package newbank.server;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewBank {
  
    private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;

    private static final String successString = "SUCCESS";
    private static final String failString = "FAIL";

    private NewBank() {
        customers = new HashMap<>();
        addTestData();
    }

    private void addTestData() {
        Customer bhagy = new Customer("1234");
        bhagy.addAccount(new Account("Main", 1000.0));
        customers.put("Bhagy", bhagy);

        Customer christina = new Customer("Tina01");
        christina.addAccount(new Account("Main", 800.0));
        christina.addAccount(new Account("Savings", 1500.0));
        customers.put("Christina", christina);

        Customer john = new Customer("JohnDoe");
        john.addAccount(new Account("Main", 800.0));
        john.addAccount(new Account("Checking", 250.0));
        customers.put("John", john);

        Customer marc = new Customer("password");
        marc.addAccount(new Account("Main", 134));
        marc.addAccount(new Account("Savings", 89));
        marc.addAccount(new Account("BottlecapsStash", 1645));
        customers.put("Marc", marc);

        Customer wayne = new Customer("1234");
        wayne.addAccount(new Account("Main", 134));
        wayne.addAccount(new Account("Savings", 89));
        wayne.addAccount(new Account("testing", 1645));
        customers.put("Wayne", wayne);

    }

    public static NewBank getBank() {
        return bank;
    }

    public synchronized CustomerID checkLogInDetails(String userName, String password) {
        if (customers.containsKey(userName) && password.equals(customers.get(userName).getPassword())) {
            return new CustomerID(userName);
        }
        return null;
    }

    /**
     * checks whether a particular username has already been used
     * @param username is the username to check
     * @return whether it has been used already
     */
    private boolean isExistingCustomer(String username) {
        AtomicBoolean isExisting = new AtomicBoolean(false);
        customers.forEach((s,cus) ->{
            if(s.toUpperCase().equals(username.toUpperCase())){
                isExisting.set(true);
            }
        });
        return (isExisting.get());
    }

    // commands from the NewBank customer are processed in this method
    public synchronized String processRequest(CustomerID customer, String request) {
        String command;
        if (request.contains(" ")) {
            command = request.substring(0, request.indexOf(" "));
        } else {
            command = request;
        }

        System.out.println("Command: " + command);

        if (customers.containsKey(customer.getKey())) {
            switch (command) {
                case "SHOWMYACCOUNTS":
                    return showMyAccounts(customer);
                case "MOVE":
                    return moveFunds(customer, request);
                case "HELP":
                    return showHelp();
                case "PAY":
                    return sendMoney(customer, request);
                case "NEWACCOUNT":
                    return newAccount(customer,request);
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
            Customer customer = new Customer(password);
            customer.addAccount(new Account("Main", 0.0));
            if(isExistingCustomer(userName)){
                return false;
            } else {
                customers.put(userName, customer);
                return true;
            }
        } catch (Error e) {
            return false;
        }
    }

    private String showMyAccounts(CustomerID customer) {
        return (customers.get(customer.getKey())).accountsToString();
    }

    /**
     * creates a help message for a customer
     *
     * @return the string with the help message
     */
    private String showHelp() {
        return ("Please select one of the following options: \n" +
                "1) To view your accounts enter SHOWMYACCOUNTS \n" +
                "2) To transfer funds between your accounts, enter MOVE followed by the sum and then the two account names and sum. e.g. MOVE 10 Main Savings \n" +
                "3) To pay others customers from your Main account to their Main account, enter PAY followed by that person's name and the amount. e.g. PAY John 100" +

                "\n0) To exit this menu and close down the program, enter EXIT \n"
        );
    }

    private String sendMoney(CustomerID payerID, String request) {
        String[] words = request.split(" ");
        double amount = 0;
        Customer payee = null;
        Customer payer = customers.get(payerID.getKey());
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
                payee = customers.get(words[i]);
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


    private String moveFunds(CustomerID customer, String request) {

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

    private Account findCustomerAccount(CustomerID customerID, String accountName) {

        Customer customer = customers.get(customerID.getKey());

        if (customer==null){
            return null;
        } else {
            return customer.getAccounts().get(accountName);
        }

    }

	private String newAccount (CustomerID customerID, String request) {
		
    	String[] words = request.split(" ");

    	for (int i = 0; i < words.length; i++){

    	    if (i==0){
    	        // ignore the command word
                continue;
    	    } else if (i==1){

                // Second word from split string is accountName
    	        String accountName = words[i];

    	        System.out.println("New Account Name: " + accountName);

    	        Customer customer = customers.get(customerID.getKey());
    				
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
