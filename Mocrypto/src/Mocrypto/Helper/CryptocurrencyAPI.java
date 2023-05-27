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

/*
   public double getExchangeRate1(String fromCurrency, String toCurrency){
    // First parameter is FROM currency second one is TO currency!

       double exchangeRate = 0.0; // Store the exchange rate in a double variable

       try {
           // Send request to the API
           HttpRequest request = HttpRequest.newBuilder()
                   .uri(URI.create("https://alpha-vantage.p.rapidapi.com/query?from_currency="+fromCurrency+"&function=CURRENCY_EXCHANGE_RATE&to_currency="+toCurrency))
                   .header("X-RapidAPI-Key", "f4dc7b4755mshabeb5efe49cbb98p1ed3d1jsn997e6463d8b0")
                   .header("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
                   .method("GET", HttpRequest.BodyPublishers.noBody())
                   .build();
           // Get the response from the API
           HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
           System.out.println(response.body());

           // Store the response string
           String jsonString = response.body();
           //Create a Json Object to parse the string
           JSONObject jsonObject = new JSONObject(jsonString);
           // Store the Json Object
           JSONObject exchangeRateObj = jsonObject.getJSONObject("Realtime Currency Exchange Rate");
           // Get the exchange rate in double form from the relevant child
           exchangeRate = Double.parseDouble(exchangeRateObj.getString("5. Exchange Rate"));

           System.out.println("Exchange Rate : " + exchangeRate);

       }catch (InterruptedException | IOException e) {
           throw new RuntimeException(e);
       }

       return exchangeRate;
   }


 */

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

       int count = 1;

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

           if (count == 3)
               break;

           count++;
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
                    .uri(URI.create("https://coinranking1.p.rapidapi.com/coin/"+cryptocurrency.getUuid()+"/price?referenceCurrencyUuid=yhjMzLPhuIDl"))
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




    /*

    public void getAPICryptocurrencies(){
        HashMap<String,String> cryptocurrencies = new HashMap<String,String>();
       try {
           FileReader fr = new FileReader("./digital_currency_list.csv");
           BufferedReader br = new BufferedReader(fr);
           br.readLine(); // Skip the header line
           String line = br.readLine(); // Get the first line
           while (line != null){ // Iterate through the file
               int indexOfComma = line.indexOf(","); // Divide the line checking comma
               // Store abbreviation and full name of the cryptocurrency
               String abbreviation = line.substring(0,indexOfComma);
               String fullName = line.substring(indexOfComma+1);
               // Put them into the hash map
               cryptocurrencies.put(abbreviation,fullName);
               // Get the next line
               line = br.readLine();
           }
       }catch (IOException e) {
           System.out.println(e);
       }
        System.out.println(cryptocurrencies);
        System.out.println(cryptocurrencies.size());
    }

     */
}
