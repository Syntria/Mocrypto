package Mocrypto.Helper;

import Mocrypto.Model.Cryptocurrency;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class CryptocurrencyAPI {

   public CryptocurrencyAPI(){

   }

   public ArrayList<Cryptocurrency> getCryptocurrencyList(){

       ArrayList<Cryptocurrency> cryptocurrencies = new ArrayList<>();

       try {
           // The documentation of this query
           //https://developers.coinranking.com/api/documentation/coins#get-coins
       HttpRequest request = HttpRequest.newBuilder()
               .uri(URI.create("https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=24h&tiers%5B0%5D=1&orderBy=marketCap&orderDirection=desc&limit=50&offset=0"))
               .header("X-RapidAPI-Key", "f4dc7b4755mshabeb5efe49cbb98p1ed3d1jsn997e6463d8b0")
               .header("X-RapidAPI-Host", "coinranking1.p.rapidapi.com")
               .method("GET", HttpRequest.BodyPublishers.noBody())
               .build();
       HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

       // Store the response string
       String jsonString = response.body();
       //Create a Json Object to parse the string
       JSONObject jsonObject = new JSONObject(jsonString);
       JSONObject dataObject = jsonObject.getJSONObject("data");
       // Create the coins array(the API provide the data in this way)
       JSONArray coins = dataObject.getJSONArray("coins");

        // Iterate the coin structures
       for(Object object : coins){
           // Store the line as a JSON object
           JSONObject coin = (JSONObject) object;
           // Split them into their attributes
           String symbol = (String) coin.get("symbol");
           String name = (String) coin.get("name");
           String uuid = (String) coin.get("uuid");
           double price = Double.parseDouble((String) coin.get("price"));
           double volume = Double.parseDouble((String) coin.get("24hVolume"));
           // Create a cryptocurrency object
           Cryptocurrency cryptocurrency = new Cryptocurrency(uuid,name,symbol,price,volume);
           // Add it to the cryptocurrencies list
           cryptocurrencies.add(cryptocurrency);
       }

       }catch (InterruptedException | IOException e) {
           throw new RuntimeException(e);
       }

       return cryptocurrencies;
   }



    public double getExchangeRate(Cryptocurrency fromCoin, Cryptocurrency toCoin){
       double exchangeRate = -1.0;
        try {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://coinranking1.p.rapidapi.com/coin/"+fromCoin.getUuid()+"/price?referenceCurrencyUuid="+toCoin.getUuid()))
                .header("X-RapidAPI-Key", "f4dc7b4755mshabeb5efe49cbb98p1ed3d1jsn997e6463d8b0")
                .header("X-RapidAPI-Host", "coinranking1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            // Store the response string
            String jsonString = response.body();
            //Create a Json Object to parse the string
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            exchangeRate = Double.parseDouble((String) dataObject.get("price"));

        }catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
       return exchangeRate;
    }


    public double getExchangeRate(Cryptocurrency cryptocurrency){
       double exchangeRate = -1.0;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://coinranking1.p.rapidapi.com/coin/"+cryptocurrency.getUuid()+"/price?referenceCurrencyUuid=HIVsRcGKkPFtW"))
                    .header("X-RapidAPI-Key", "f4dc7b4755mshabeb5efe49cbb98p1ed3d1jsn997e6463d8b0")
                    .header("X-RapidAPI-Host", "coinranking1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            // Store the response string
            String jsonString = response.body();
            //Create a Json Object to parse the string
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            // Get the exchange rate
            exchangeRate = Double.parseDouble((String) dataObject.get("price"));

        }catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        return exchangeRate;
    }
}
