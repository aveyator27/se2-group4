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
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer("JohnDoe");
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);

		Customer marc = new Customer("password");
		marc.addAccount(new Account("Main", 134));
		marc.addAccount(new Account("Savings", 89));
		marc.addAccount(new Account("Secret Bottlecaps Stash", 132341645));
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
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
