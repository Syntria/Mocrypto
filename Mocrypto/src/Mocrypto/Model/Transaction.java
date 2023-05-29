package Mocrypto.Model;

import java.time.Instant;

public class Transaction {
    private String type;
    private int id;
    private double amount;
    private String timestamp;
    private Cryptocurrency targetCryptocurrency;
    private Cryptocurrency baseCryptocurrency;
    private int recipientAddress;



    public Transaction(String type, double amount,
                       Cryptocurrency targetCryptocurrency, Cryptocurrency baseCryptocurrency){
        this.type = type;
        this.amount = amount;
        this.targetCryptocurrency = targetCryptocurrency;
        this.baseCryptocurrency = baseCryptocurrency;
        this.timestamp = Instant.now().toString();
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Cryptocurrency getTargetCryptocurrency() {
        return targetCryptocurrency;
    }

    public void setTargetCryptocurrency(Cryptocurrency targetCryptocurrency) {
        this.targetCryptocurrency = targetCryptocurrency;
    }

    public Cryptocurrency getBaseCryptocurrency() {
        return baseCryptocurrency;
    }

    public void setBaseCryptocurrency(Cryptocurrency baseCryptocurrency) {
        this.baseCryptocurrency = baseCryptocurrency;
    }
    public int getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(int recipientAddress) {
        this.recipientAddress = recipientAddress;
    }
}
