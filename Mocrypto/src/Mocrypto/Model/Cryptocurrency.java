package Mocrypto.Model;

public class Cryptocurrency {

    private final String uuid;
    private String name;
    private String shortname;
    private double price;
    private double volume;
    private double amount;

    public Cryptocurrency(String uuid, String name, String shortname, double price,double volume) {
        this.uuid = uuid;
        this.name = name;
        this.shortname = shortname;
        this.price = price;
        this.volume = volume;
        this.amount = 0;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void addAmount(double amount){
        this.amount = getAmount() + amount;
    }
}