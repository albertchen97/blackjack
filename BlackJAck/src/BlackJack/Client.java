package BlackJack;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JOptionPane;

// Client class
public class Client {

    // driver code
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int port = 8000;
        String host = "localhost";
        // Initiate the UI
        Account account = new Account();
        ClientGUI clientGUI = new ClientGUI(account);
        Dealer dealer = new Dealer();
        Player player = new Player(account.getUserID());

        // Establish the connection with server
        try (Socket socket = new Socket(host, port)) {

            System.out.println("Connected to server" + host + ":" + port);

            // Writing to server
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            // Reading from server
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            // Get user authentication information (username and password) from GUI
            System.out.println("Getting user authentication information");
            UserAuthentication userAuthentication = clientGUI.loginCommands();
            System.out.println(userAuthentication.getUsername());

            // Send user authentication information to server to check if the user is valid
            objectOutputStream.writeUnshared(userAuthentication);
            // objectOutputStream.flush();

            System.out.println("Sent to server");

            // Listen to server's response
            while ((userAuthentication = (UserAuthentication) objectInputStream.readObject()) != null) {
                System.out.println("Client: " + userAuthentication.getUsername());
                // Check if the user is logged in
                if (userAuthentication.getType() == UserAuthenticationType.LOGGED_IN) {
                    account = (Account) objectInputStream.readObject();
                    account.setAccountStatus(AccountStatus.ONLINE);
                    System.out.println("User " + userAuthentication.getUsername() + " logged in");
                    break;
                } else if (userAuthentication.getType() == UserAuthenticationType.NAME_TAKEN) {
                    System.out.println("Name Taken");
                }

                userAuthentication = clientGUI.loginCommands();
                objectOutputStream.writeUnshared(userAuthentication);
            }

            // print out the account information
            System.out.println(account.getUserID());
            System.out.println(account.getBalance());
            do {
                // Home page

                // Pop up the home page and pass the account information (username,
                // password, and balance) from GUI
                account = clientGUI.accountCommands(account);
                System.out.println(account.getAccountAction());
                // Send the account information to the server
                objectOutputStream.writeUnshared(account);

                objectOutputStream.flush();
                account = (Account) objectInputStream.readObject();

                // Listen to the server's response (see if the server has received the account
                // and stored it successfully)
                System.out.println("Entering the account part");

                while (account.getAccountAction() != AccountAction.LOG_OUT) {
                    if (account.getAccountAction() == AccountAction.UPDATE_BALANCE) {
                        account = clientGUI.accountCommands(account);
                    } else if (account.getAccountAction() == AccountAction.PLAY_GAME) {
                        // If the user clicks the play button, break the loop and start the game
                        break;
                    } else if (account.getAccountAction() == AccountAction.LOG_OUT) {
                        // If the user clicks the logout button, break the loop and go back to the log
                        // in page
                        // account = clientGUI.accountCommands(account);

                        userAuthentication = clientGUI.loginCommands();
                        break;
                    }
                    // // Otherwise, keep popping up the home page
                    // account = clientGUI.accountCommands(account);
                    // // Otherwise, keep the server updated with the latest account information
                    // objectOutputStream.writeUnshared(account);
                    // // objectOutputStream.flush();
                }

                System.out.println("Listening to the user's actions on the home page...");

                // Set player's round balance (the amount of money they have when they start the
                // round)
                player.setRoundBalance(account.getBalance());

                Table roundTable = new Table();
                String bet = "";
                roundTable.addPlayer(player);
                while (roundTable.getEOR() != true) {
                    while (bet.equals("")) {
                        bet = JOptionPane.showInputDialog(null, "Enter a bet:");
                    }
                    roundTable.playRound();

                    // Check if the user has enough money to bet
                    while (Integer.parseInt(bet) > account.getBalance()) {
                        bet = JOptionPane.showInputDialog(null, "You have $" + account.getBalance()
                                + " in your account. \n\nYou don't have enough money to bet $ " + bet
                                + "! \n\nPlease enter a lower bet:");
                        System.out.println("Client: Bet = " + bet);
                        System.out.println("Client: Invalid bet!");
                        if (Integer.parseInt(bet) <= account.getBalance()) {
                            System.out.println("Client: Bet = " + bet);
                            System.out.println("Client: Valid bet! Starting the game...");
                            break;
                        }
                    }
                    System.out.println("Client: Setting up round table.");
                    roundTable.getPlayer(0).getPlayerHand().setBet(Integer.parseInt(bet));
                    // Update the account balance
                    account.setBalance(account.getBalance() - Integer.parseInt(bet));
                    System.out.println("Client: Round table is set up, ready to play!");
                    System.out.println("Client: Round table EOR = " + roundTable.getEOR());

                    // while (roundTable.getEOR() != true) {
                    clientGUI.inGame(player, roundTable);
                    // // Initialize the round balance
                    // player.setRoundBalance(account.getBalance());
                    System.out.println("Client: Account balance = " + account.getBalance());
                    // Kick out the player if they run out of money
                    if (account.getBalance() <= 0) {
                        System.out.println("Client: Player ran out of money.");
                        JOptionPane.showMessageDialog(null, "You ran out of money! \nYou are kicked out of the game!");
                        player.setPlayerStatus(PlayerStatus.EMPTY);
                        break;
                    }
                }
                // account = clientGUI.accountCommands(account);
            } while (account.getAccountStatus() != AccountStatus.OFFLINE);
        }
    }
}