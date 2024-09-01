package BlackJack;
import javax.swing.JOptionPane;
import java.io.Serializable;

public class Account implements Serializable {
    private UserAuthentication userIDPassword;
    private int balance;
    private AccountStatus accountStatus;
    private AccountAction accountAction;

    public Account() {
        this.userIDPassword = new UserAuthentication(null, null, UserAuthenticationType.UNDEFINED);
        this.balance = 0;
        this.accountStatus = AccountStatus.OFFLINE;
    }

    public Account(UserAuthentication newUser, int newBalance, AccountStatus newAcctStatus) {
        this.userIDPassword = newUser;
        this.balance = newBalance;
        this.accountStatus = newAcctStatus;
    }

    public boolean login(UserAuthentication user) {
        if (user.getUsername().equals(this.userIDPassword.getUsername())
                && user.getPassword().equals(this.userIDPassword.getPassword())) {
            this.accountStatus = AccountStatus.ONLINE;
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }
    }

    public void signUp(UserAuthentication user) {
        this.userIDPassword = user;
        this.accountStatus = AccountStatus.ONLINE;
        System.out.println("Account created.");
    }

    public void logOut() {
        this.accountStatus = AccountStatus.OFFLINE;
        System.out.println("Logged out.");
    }

    public UserAuthentication getUser() {
        return this.userIDPassword;
    }

    public void setUser(UserAuthentication loginCommands) {
        this.userIDPassword = loginCommands;
    }

    public String getUserID() {
        return this.userIDPassword.getUsername();
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void addBalance(int amount) {
        this.balance += amount;
        System.out.println(amount + " added to balance. New balance: " + this.balance);
    }

    public void withdrawBalance(int amount) {
        if (amount > this.balance) {
            System.out.println("Insufficient funds.");
            JOptionPane.showMessageDialog(null, "Insufficient funds.");
        } else {
            this.balance -= amount;
            System.out.println(amount + " withdrawn from balance. New balance: " + this.balance);
            JOptionPane.showMessageDialog(null,
                    "Withdrawn " + amount + " from balance. \n\nNew balance: " + this.balance);
        }
    }

    public void setAccountStatus(AccountStatus newAccountStatus) {
        this.accountStatus = newAccountStatus;
    }

    public void updateAccountStatus() {
        if (this.balance < 0) {
            this.accountStatus = AccountStatus.OFFLINE;
            System.out.println("Account status updated to OFFLINE due to negative balance.");
        }
    }

    public AccountStatus getAccountStatus() {
        return this.accountStatus;
    }

    public void updateAccountAction(AccountAction newAccountAction) {
        this.accountAction = newAccountAction;
    }

    public AccountAction getAccountAction() {
        return accountAction;
    }

    /*
     * public char[] getPassword() {
     * return null;
     * }
     */
}
