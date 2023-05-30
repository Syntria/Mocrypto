package Mocrypto.Model;

import Mocrypto.Helper.CryptocurrencyAPI;
import Mocrypto.Helper.Helper;
import Mocrypto.View.MainPage;

import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.Iterator;

public class Exchange {
    private Cryptocurrency targetCryptocurrency;
    private Cryptocurrency baseCryptocurrency;

    private User user;


    public Exchange(User user, Cryptocurrency targetCryptocurrency, Cryptocurrency baseCryptocurrency){
        this.user = user;
        this.targetCryptocurrency = targetCryptocurrency;
        this.baseCryptocurrency = baseCryptocurrency;
    }



    public Portfolio addCryptoCurrencyToPortfolio(Portfolio portfolio,double amount){
        // Get the coin list
        ArrayList<Cryptocurrency> cryptocurrencies = portfolio.getCryptocurrencies();

        Iterator<Cryptocurrency> iterator = cryptocurrencies.iterator();
        boolean contains = false;
        int indexCounter = 0; // Store the index of the coin if occurs
        while (iterator.hasNext()){ // Check if the target coin is already bought
            Cryptocurrency check = iterator.next();
            if (check.getShortname().equals(targetCryptocurrency.getShortname())){
                contains = true;
                break;
            }
            indexCounter++;
        }
        if (contains) {
            // Add the new amount to the coin (update the amount)
            cryptocurrencies.get(indexCounter).addAmount(amount);
        }
        else{ // If the target coin is not in the user's portfolio
            // Create a new coin object not to change original coin list
            Cryptocurrency cryptocurrency = targetCryptocurrency;
            // Add the amount to the coin
            cryptocurrency.setAmount(amount);
            // Add the coin to user's portfolio
            cryptocurrencies.add(cryptocurrency);
        }
        // Update the user's portfolio list
        portfolio.setCryptocurrencies(cryptocurrencies);

        return portfolio;
    } // end addCryptoCurrencyToPortfolio

    public Transaction buyCryptocurrency(double baseCoinAmount, String type){

        Portfolio portfolio = user.getPortfolio();

        int baseCoinIndex = getIndexOfCurrency(portfolio.getCryptocurrencies(),baseCryptocurrency.getShortname());
        if(baseCoinIndex == user.getPortfolio().getCryptocurrencies().size()){
            Helper.showMsg(baseCryptocurrency.getShortname() + " isn't available in your portfolio!");
            return null;
        }
        Cryptocurrency baseCryptocurrencyInPortfolio = portfolio.getCryptocurrencies().get(baseCoinIndex);

        if(baseCoinAmount <= baseCryptocurrencyInPortfolio.getAmount()) { // If user has sufficient balance
            CryptocurrencyAPI API = new CryptocurrencyAPI();
            double targetCoinAmount = baseCoinAmount * 1/API.getExchangeRate(targetCryptocurrency,baseCryptocurrency); // Store the amount of the target coin
            // Add the target coin to the user portfolio
            portfolio = addCryptoCurrencyToPortfolio(portfolio,targetCoinAmount);

            // Decrease the amount of the base coin
            baseCryptocurrencyInPortfolio = portfolio.getCryptocurrencies().get(baseCoinIndex);
            double oldAmount = baseCryptocurrencyInPortfolio.getAmount();
            if(baseCoinAmount == oldAmount){ // If all the balance of the base coin is spending
                portfolio.getCryptocurrencies().remove(baseCryptocurrencyInPortfolio); // remove the coin from the portfolio
            }
            else {
                portfolio.getCryptocurrencies().get(baseCoinIndex).setAmount(oldAmount-baseCoinAmount); // decrease the amount of the base coin
            }
            // Update the user portfolio
            user.setPortfolio(portfolio);

            // Create a transaction
            Transaction transaction = new Transaction(type,targetCoinAmount,targetCryptocurrency.getShortname(),baseCryptocurrency.getShortname());
            return transaction;
        }
        else{
            Helper.showMsg("Insufficient Balance!");
            return null;
        }
     } // end buyCryptocurrency



    public static int getIndexOfCurrency (ArrayList<Cryptocurrency> cryptocurrencies, String shortName){
        Iterator<Cryptocurrency> iterator = cryptocurrencies.iterator();
        int indexCounter = 0; // Store the index of the coin if occurs
        while (iterator.hasNext()){ // Check if the target coin is already bought
            Cryptocurrency check = iterator.next();
            if (check.getShortname().equals(shortName)){
                break;
            }
            indexCounter++;
        }
        return indexCounter;
    }
}
