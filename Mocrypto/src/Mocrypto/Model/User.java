package Mocrypto.Model;

public class User extends Account{

    double balance = 10000;

    public User() {
        super.setPortfolio(new Portfolio());
        Cryptocurrency cryptocurrency = new Cryptocurrency("HIVsRcGKkPFtW","Tether USD","USDT",1.0,18942563028.0);
        super.getPortfolio().getCryptocurrencies().add(cryptocurrency);
        super.getPortfolio().getCryptocurrencies().get(0).setAmount(balance);
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
