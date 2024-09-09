package BlackJack;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

// Server class
class Server {

    public static void main(String[] args) {
        ServerSocket server = null;
        int port = 8000;

        try {
            server = new ServerSocket(port);
            System.out.println("Server has been created.");
            System.out.println("Port " + port + " is listening........");
            server.setReuseAddress(true);
            House house = new House();
            Account[] accountList = new Account[100];


            // running infinite loop for getting
            // client request
            // Create a thread pool
            ExecutorService executorService = Executors.newFixedThreadPool(8);
            while (true) {
                // socket object to receive incoming client
                // requests
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected: "
                        + client.getInetAddress()
                        .getHostAddress());

                // create a new thread object
                ClientHandler clientHandler = new ClientHandler(client);

                try {
                    executorService.execute(clientHandler);
                } catch (RejectedExecutionException e) {
                    System.out.println(e.getMessage());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                    System.out.println("Server is closed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // TODO: Create a MySQL database
        // TODO: Implement a JDBC
        File file = new File(".\\src\\Playerlist.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String username = line;
                String password = br.readLine();
                // Do something with the username and password, e.g. print them out
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
            }
        } catch (IOException e) {
            System.out.println("input output error");
            System.out.println(e.getMessage());
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private boolean logged = false;

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {

            try {

                System.out.println("get output");
                // Output stream socket & Create object output stream from the output stream to
                // send an object through it
                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                // get the input stream from the connected socket & create a ObjectInputStream
                // so we can read data from it.
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                System.out.println("get intput");

                Account account = new Account(); // change
                Player player = null;
                Dealer dealer = null;

                UserAuthentication userAuthentication = null;

                System.out.println("Server: Entering the loop");

                // read in the account class
                while ((userAuthentication = (UserAuthentication) objectInputStream.readObject()) != null) {

                    System.out.println(userAuthentication.getUsername());
                    if (userAuthentication.getType() == UserAuthenticationType.LOGIN_REQUEST) {
                        // TODO: Loop through the account list and check if the username and password
                        // already exist
                        // loop the account array
                        if (userAuthentication.authenticate(userAuthentication)) {

                            // account = accountlist[i];
                            userAuthentication.setType(UserAuthenticationType.LOGGED_IN);
                            objectOutputStream.writeUnshared(userAuthentication);
                            objectOutputStream.flush();
                            // write to account based on accountList
                            objectOutputStream.writeUnshared(account);
                            objectOutputStream.flush();
                            logged = true;
                            break;
                        }

                    } else if (userAuthentication.getType() == UserAuthenticationType.SIGNUP) {
                        // check it to a list with a for loop
                        // if it's uA == um form list
                        try {
                            FileOutputStream fos = new FileOutputStream("Playerlist.txt", true);
                            String data = userAuthentication.getUsername() + "," + userAuthentication.getPassword()
                                    + "\n";
                            fos.write(data.getBytes());
                            fos.close();
                        } catch (IOException e) {
                            System.out.println("output to file error");
                        }
                        userAuthentication.setType(UserAuthenticationType.NAME_TAKEN);
                        objectOutputStream.writeUnshared(userAuthentication);
                    } else {
                        // returns it as undefine
                        objectOutputStream.writeUnshared(userAuthentication);
                    }

                }

                // check the log in
                // buf.login(buf.getUser());
                // if worked pass a log in /set logged to true
                // if nnot break (repeat the loop)

                account = (Account) objectInputStream.readObject();
                // do stuff
                objectOutputStream.writeUnshared(account);

                // home page actions play, edit funds, exit
                // play needs network
                // edit can be done localy on the clint
                // exit can send a good by and kill tread
                player = new Player(userAuthentication.getUsername());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            // Close the connection
            finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}