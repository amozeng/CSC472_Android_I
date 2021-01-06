package com.amozeng.a3_stockwatch.Api;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a3_stockwatch.MainActivity;
import com.amozeng.a3_stockwatch.Stock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StockDownloader implements Runnable{

    private static final String TAG = "StockDownloader";
    private static final String STOCK_URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String TOKEN = "/quote?token=sk_a791fb26a2384fc19d45b27d1933b589";

    private MainActivity mainActivity;
    private String searchTarget;

    public StockDownloader(MainActivity mainActivity, String searchTarget){
        this.mainActivity = mainActivity;
        this.searchTarget = searchTarget;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(STOCK_URL + searchTarget + TOKEN).buildUpon();
        //uriBuilder.appendQueryParameter("fullText", "true");
        String urlToUse = uriBuilder.toString();

        Log.d(TAG, "run: " + urlToUse);

        StringBuilder stringBuilder = new StringBuilder();

        try{
            URL url = new URL(urlToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK" + connection.getResponseCode());
                return; // stop everything
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while( (line = reader.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            Log.d(TAG, "run: "+ stringBuilder.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
        }

        process(stringBuilder.toString());
        //Log.d(TAG, "run: ");  // why this?
    }

    private void process(String s){
        try{
            //JSONArray jArray = new JSONArray(s);
            //JSONObject jStock = (JSONObject) jArray.get(0);
            JSONObject jStock = new JSONObject(s);

            final String stockSymbol = jStock.getString("symbol");
            String companyName = jStock.getString("companyName");

            String p = jStock.getString("latestPrice");
            double price = 0.0;
            if(!p.trim().isEmpty()) price = Double.parseDouble(p);

            String pC = jStock.getString("change");
            double priceChange = 0.0;
            if(!pC.trim().isEmpty()) priceChange = Double.parseDouble(pC);

            String cP = jStock.getString("changePercent");
            double changePercent = 0.0;
            if(!cP.trim().isEmpty()) changePercent = Double.parseDouble(cP);

            final Stock stock = new Stock(stockSymbol, companyName, price, priceChange, changePercent);

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.addStock(stock);
                    Log.d(TAG, "run: finished adding:  " + stockSymbol);
                }
            });

        }catch(Exception e){
            Log.e(TAG, "parseJSON: "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
