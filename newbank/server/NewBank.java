package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer("1234");
		bhagy.addAccount(new Account("Main",1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer("Tina01");
		christina.addAccount(new Account("Main",800.0));
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer("JohnDoe");
		john.addAccount(new Account("Main",800.0));
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);

		Customer marc = new Customer("password");
		marc.addAccount(new Account("Main", 134));
		marc.addAccount(new Account("Savings", 89));
		marc.addAccount(new Account("Secret Bottlecaps Stash", 1645));
		customers.put("Marc",marc);

	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName) && password.equals(customers.get(userName).getPassword())) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		String command;
		if (request.contains(" ")){
			command = request.substring(0, request.indexOf(" "));
		} else {
			command = request;
		}

		System.out.println("Command: " + command);

		if(customers.containsKey(customer.getKey())) {
			switch(command) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			case "MOVE" 		  : return transferFunds(customer, request);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String transferFunds(CustomerID customer, String request){

		double amount = 0;
		Account from = null;
		Account to = null;

		String[] words = request.split(" ");

		for (int i = 0; i < words.length; i++){
			if (i==0){
				// ignore the command word
				continue;
			} else if (i==1){
				amount = Double.valueOf(words[i]);
				System.out.println("Amount: " + Double.toString(amount));
			} else if (i==2){
				from = findCustomerAccount(customer,words[i]);
				System.out.println("From: " + words[i]);
			} else if (i==3){
				to = findCustomerAccount(customer,words[i]);
				System.out.println("To: " + words[i]);
			}
		}

		if (amount==0 || from==null || to==null){
			System.out.println("Error: Request incomplete.");
			return "FAIL";
		}

		if (from.withdraw(amount)){
			to.deposit(amount);
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}

	private Account findCustomerAccount(CustomerID customer, String accountName){

		return customers.get(customer.getKey()).getAccounts().get(accountName);

	}
	
	
	private String newAccount (CustomerID customer, String request) {
		
    	String[] words = request.split(" ");
        
        
		if (customers.get(customer.getKey()).getAccounts().get(Account(accountName)).containsKey(words[1])) {
            return "FAIL";
		} else {
    		for (int i = 0; i < words.length; i++){
    			if (i==0){
    				// ignore the command word
    				continue;
    			} else if (i==1){
    				customers.get(customer.getKey()).addAccount(new Account((words[1]),0));
    				// Return second word from split string 
    				return "SUCCESS";
    			} else if (i>=2){
    				System.out.println("Account name must only contain one word");
    				return "FAIL";
    			}
    		}
		}
    		return "FAIL";
    		
    		
    	}
    
    
}
