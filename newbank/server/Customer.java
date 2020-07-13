package newbank.server;

import java.util.HashMap;
import java.util.Map;

public class Customer {

	/*
	Password field: though "in the wild" it would be bad practice to handle passwords without proper security,
	we consider such a problem out-of-scope for the current exercise.
	*/
	private String password;
	
	private HashMap<String,Account> accounts;
	
	public Customer(String password) {
		this.password = password;
		accounts = new HashMap<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Map.Entry<String,Account> entry : accounts.entrySet()) {
			s += entry.getValue().toString();
		}
		return s;
	}
	public String getPassword(){
		return password;
	}

	public void addAccount(Account account) {
		accounts.put(account.getAccountName(), account);
	}

	public HashMap<String, Account> getAccounts(){
		return accounts;
	}

}
