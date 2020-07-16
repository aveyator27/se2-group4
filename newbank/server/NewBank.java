package newbank.server;

import java.util.HashMap;

public class NewBank {

	private static final NewBank bank = new NewBank();
	private HashMap<String, Customer> customers;

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
		marc.addAccount(new Account("Secret Bottlecaps Stash", 1645));
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
					return transferFunds(customer, request);
				case "HELP":
					return showHelp();
				case "PAY":
					return sendMoney(customer, request);

				default:
					return "FAIL";
			}
		}
		return "FAIL";
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
			customers.put(userName, customer);
			return true;
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
		return ("Please select one of the following options: \n " +
				"1) To view your accounts enter SHOWMYACCOUNTS \n " +
				"2) To transfer funds, enter MOVE followed by the two account names and sum \n " +
				"3) To exit this menu and close down the program, press EXIT \n" +
				"4) To pay others customer to their main account use <PAY Person amount>. e.g. PAY John 100"
				);
	}


	private String sendMoney(CustomerID payer, String request) {
		String[] words = request.split(" ");
		double amount = 0;
		Account payeeMain = null;
		Account payerMain = customers.get(payer.getKey()).getAccounts().get("Main");
		System.out.println("1");

		for (int i = 0; i < words.length; i++) {
			if (i == 0) {
				continue;
			} else if (i == 1) {
				payeeMain = customers.get(words[i]).getAccounts().get("Main");
				if (payerMain == null||payeeMain == null ){
					System.out.println("Error: Request incomplete.");
					return "Fail";
				}
			} else if (i == 2) {
				amount = Double.valueOf(words[i]);
				if (amount == 0){
					System.out.println("Transaction invalid");
					return "Fail";
				}
			}
		}

		if (payerMain.withdraw(amount)) {
			payeeMain.deposit(amount);
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}




	private String transferFunds(CustomerID customer, String request) {

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
				System.out.println("Amount: " + Double.toString(amount));
			} else if (i == 2) {
				from = findCustomerAccount(customer, words[i]);
				System.out.println("From: " + words[i]);
			} else if (i == 3) {
				to = findCustomerAccount(customer, words[i]);
				System.out.println("To: " + words[i]);
			}
		}

		if (amount == 0 || from == null || to == null) {
			System.out.println("Error: Request incomplete.");
			return "FAIL";
		}

		if (from.withdraw(amount)) {
			to.deposit(amount);
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}

	private Account findCustomerAccount(CustomerID customer, String accountName) {

		return customers.get(customer.getKey()).getAccounts().get(accountName);

	}

}
//
//	private Account findPayeeAccount(CustomerID customer, String payee, String accountName){
//		return customers.get(customer.getKey()).getAccounts().get(accountName);
//	}


