package Mocrypto.Model;

public class User extends Account{

    private double balance = 10000;

    public User() {

    }

    public User(int id, String name, String surname,String username, String password, String type) {

        super(id, name, surname,username, password, type);

    }

    public double getBalance() {
        balance = super.getPortfolio().getAmountOfSpecificCoin("USDT");
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
