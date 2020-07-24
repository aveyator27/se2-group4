package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

    private NewBank bank;
    private BufferedReader in;
    private PrintWriter out;


    public NewBankClientHandler(Socket s) throws IOException {
        bank = NewBank.getBank();
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
    }

    public void run() {
        // keep getting requests from the client and processing them
        try {
            UserID user = null;
            boolean invalidChoice = true;
            while (invalidChoice) {
                //ask client whether they want to register or login
                out.println("To register please enter REGISTER. \nTo login please enter LOGIN.");
                String choice = in.readLine();
                //for registration
                if (choice.equals("REGISTER")) {
                    invalidChoice = false;
                    //ask for new username
                    out.println("Enter Username");
                    String userName = in.readLine();
                    //ask for new password
                    out.println("Enter Password");
                    String password = in.readLine();
                    //create the account
                    boolean regSuccess = bank.createCustomer(userName, password);
                    //if registered, automatically authenticate user and login
                    if (regSuccess) {
                        user = bank.checkLogInDetails(userName, password);
                        out.println("Successfully registered.");
                        //otherwise display error message
                    } else {
                        out.println("Error. Not registered. Please try again.");
                        invalidChoice = true;
                    }
                    //for login
                } else if (choice.equals("LOGIN")) {
                    invalidChoice = false;
                    // ask for user name
                    out.println("Enter Username");
                    String userName = in.readLine();
                    // ask for password
                    out.println("Enter Password");
                    String password = in.readLine();
                    out.println("Checking Details...");
                    // authenticate user and get customer ID token from bank for use in subsequent requests
                    user = bank.checkLogInDetails(userName, password);
                    //if no valid menu option was selected
                } else {
                    out.println("Invalid choice. Please try again.");
                }
            }
            // if the user is authenticated then get requests from the user and process them
            if (user != null) {
                out.println("Log In Successful. What do you want to do?");
                while (true) {
                    String request = in.readLine();
                    //if the request is to exit, the program immediately exits
                    if (request.equals("EXIT")) {
                        break;
                    } else {
                        //otherwise, any other command is processed
                        System.out.println("Request from " + user.getKey());
                        String response = bank.processRequest(user, request);
                        out.println(response);
                    }
                }
            } else {
                out.println("Log In Failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

}
