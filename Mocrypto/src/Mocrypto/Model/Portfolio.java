package Mocrypto.Model;

import Mocrypto.Helper.Helper;

import java.util.ArrayList;
import java.util.Iterator;

public class Portfolio {
    private String name;
    private int id;
    private int address;
    private double currentValue = 0;
    private ArrayList<Cryptocurrency> cryptocurrencies;


    public Portfolio(){
        this.cryptocurrencies = new ArrayList<Cryptocurrency>();
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCurrentValue() {
        double totalAmount = 0.0;

        assert cryptocurrencies != null;
        for (Cryptocurrency cryptocurrency : this.cryptocurrencies){
            totalAmount += cryptocurrency.getPrice();
        }
        this.currentValue = totalAmount;
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public ArrayList<Cryptocurrency> getCryptocurrencies() {
        return cryptocurrencies;
    }

    public void setCryptocurrencies(ArrayList<Cryptocurrency> cryptocurrencies) {
        this.cryptocurrencies = cryptocurrencies;
    }


    public double getAmountOfSpecificCoin(String shortName){
        Iterator<Cryptocurrency> iterator = getCryptocurrencies().iterator();
        int indexCounter = 0; // Store the index of the coin if occurs
        while (iterator.hasNext()){ // Check if the target coin is already bought
            Cryptocurrency check = iterator.next();
            if (check.getShortname().equals(shortName)){
                return check.getAmount();
            }
        }
        Helper.showMsg("The currency isn't available in your portfolio!");
        return 0.0;
    }
}
