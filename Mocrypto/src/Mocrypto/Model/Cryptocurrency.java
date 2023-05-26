package Mocrypto.Model;

public class Cryptocurrency {

    private String name;
    private String shortname;
    private int price;

    public Cryptocurrency(String name, String shortname, int price) {
        this.name = name;
        this.shortname = shortname;
        this.price = price;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
