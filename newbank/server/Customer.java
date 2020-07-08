package newbank.server;

import java.util.ArrayList;

public class Customer {

	/*
	Password field: though "in the wild" it would be bad practice to handle passwords without proper security,
	we consider such a problem out-of-scope for the current exercise.
	*/
	private String password;
	
	private ArrayList<Account> accounts;
	
	public Customer(String password) {
		this.password = password;
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public String getPassword(){
		return password;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
