package Mocrypto.Helper;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;


public class CryptocurrencyAPI {

   public CryptocurrencyAPI(){

   }

   public double getExchangeRate(String fromCurrency, String toCurrency){
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

           String[] arr = new String[]{response.body()};
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
}
