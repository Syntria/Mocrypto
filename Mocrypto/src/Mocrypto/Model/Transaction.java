package Mocrypto.Model;

import java.time.Instant;

public class Transaction {
    private String type;
    private double amount;
    private String timestamp;
    private String targetCryptocurrency;
    private String baseCryptocurrency;



    public Transaction(String type, double amount,
                       String targetCryptocurrency, String baseCryptocurrency){
        this.type = type;
        this.amount = amount;
        this.targetCryptocurrency = targetCryptocurrency;
        this.baseCryptocurrency = baseCryptocurrency;
        this.timestamp = Instant.now().toString();
    }

    public Transaction(String type, double amount,
                       String targetCryptocurrency, String baseCryptocurrency, String timestamp){
        this.type = type;
        this.amount = amount;
        this.targetCryptocurrency = targetCryptocurrency;
        this.baseCryptocurrency = baseCryptocurrency;
        this.timestamp = timestamp;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String  getTargetCryptocurrency() {
        return targetCryptocurrency;
    }

    public void setTargetCryptocurrency(String targetCryptocurrency) {
        this.targetCryptocurrency = targetCryptocurrency;
    }

    public String getBaseCryptocurrency() {
        return baseCryptocurrency;
    }

    public void setBaseCryptocurrency(String baseCryptocurrency) {
        this.baseCryptocurrency = baseCryptocurrency;
    }


}