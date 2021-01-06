package com.amozeng.a3_stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amozeng.a3_stockwatch.Api.StockDownloader;
import com.amozeng.a3_stockwatch.Api.SymbolNameDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private final List<Stock> stockList = new ArrayList<>();
    private SwipeRefreshLayout swiper;
    private RecyclerView recyclerView;
    private StockAdapter mAdapter;
    private String choice;
    private static final String STOCK_URL = "http://www.marketwatch.com/investing/stock/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStocks();
            }
        });

        // Load the initial data
        SymbolNameDownloader symbolND = new SymbolNameDownloader(this);
        new Thread(symbolND).start();


        readJSONData();
        refreshStocksFirstTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeJSONData();
    }

    private void zeroList() {
        for(Stock s: stockList){
            s.setPrice(0.0);
            s.setPriceChange(0.0);
            s.setChangePercent(0.0);
        }
    }

    private void refreshStocksFirstTime(){
        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot Be Refreshed Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            zeroList();
            swiper.setRefreshing(false);
            return;
        }
        List<Stock> tmpStockList = new ArrayList<Stock>();
        for(Stock s: stockList){
            tmpStockList.add(s);
        }
        stockList.clear();

        for(Stock s: tmpStockList){
            String symbol = s.getStockSymbol();
            StockDownloader stockDownloader = new StockDownloader(this, symbol);
            new Thread(stockDownloader).start();
        }

        swiper.setRefreshing(false);
    }

    private void refreshStocks(){

        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot Be Refreshed Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            swiper.setRefreshing(false);
            return;
        }
        SymbolNameDownloader symbolND = new SymbolNameDownloader(this);
        new Thread(symbolND).start();

        List<Stock> tmpStockList = new ArrayList<Stock>();
        for(Stock s: stockList){
            tmpStockList.add(s);
        }
        stockList.clear();

        for(Stock s: tmpStockList){
            String symbol = s.getStockSymbol();
            StockDownloader stockDownloader = new StockDownloader(this, symbol);
            new Thread(stockDownloader).start();
        }

        swiper.setRefreshing(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_add) {
            addStockDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addStockDialog(){

        if(!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot be Added Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choice = et.getText().toString().trim();
                final ArrayList<String> results = SymbolNameDownloader.findMatches(choice);

                if(results.size() == 0) {
                    doNoAnswer(choice);
                }else if(results.size() == 1) {
                    doSelection(results.get(0));
                }else{
                    String[] array = results.toArray(new String[0]);
                    AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity.this));
                    builder.setTitle("Make a selection");
                    builder.setItems(array, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String symbol = results.get(which);
                            doSelection(symbol);
                        }
                    });
                    builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // cancelled the dialog
                        }
                    });
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // cancel the dialog
            }
        });

        builder.setMessage("Please enter a Stock Symbol or Name:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doNoAnswer(String inputSymbol){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No data for specified symbol/name");
        builder.setTitle("No Data Found:" + inputSymbol);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doSelection(String searchResult){
        String[] data = searchResult.split("-");
        StockDownloader stockDownloader = new StockDownloader(this, data[0].trim());
        new Thread(stockDownloader).start();
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        String symbol = stockList.get(pos).getStockSymbol();

        Uri.Builder uriBuilder = Uri.parse(STOCK_URL + symbol).buildUpon();
        String urlToUse = uriBuilder.toString();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(STOCK_URL + symbol));
        startActivity(browserIntent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.baseline_delete_white_36);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stockList.remove(pos);
                mAdapter.notifyDataSetChanged();
                writeJSONData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // cancel the dialog, do nothing
            }
        });

        builder.setMessage("Delete " + stockList.get(pos).getStockSymbol()+ "?");
        builder.setTitle("Delete Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void showDownloadError() {
        Toast.makeText(this, "Failed download symbols/names", Toast.LENGTH_LONG).show();
    }

    private void badDataAlert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No data for selection");
        builder.setTitle("Symbol Not Found" + s);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addStock(Stock stock) {
        if(stock == null) {
            badDataAlert(choice); // pass choice to BadAlert
            return;
        }

        if(stockList.contains(stock)) { // make sure it's not repeat
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(stock.getStockSymbol() + " is already displayed.");
            builder.setTitle("Duplicate Stock");
            builder.setIcon(R.drawable.baseline_error_white_36);

            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        stockList.add(stock);
        Collections.sort(stockList);
        writeJSONData();
        mAdapter.notifyDataSetChanged();
    }



    private void readJSONData() {
        try{
            FileInputStream fis = getApplicationContext().openFileInput("DataFile.json");

            byte[] data = new byte[fis.available()]; // good for small files
            int loaded = fis.read(data);
            Log.d(TAG, "readJSONData: Loaded "+ loaded + " bytes");
            fis.close();
            String json = new String(data);

            //Create JSON Array from string file content
            JSONArray noteArr = new JSONArray(json);
            for(int i = 0; i < noteArr.length(); i++){
                JSONObject cObj = noteArr.getJSONObject(i);

                String symbol = cObj.getString("symbol");
                String companyName = cObj.getString("companyName");
                Double price = cObj.getDouble("price");
                Double priceChange = cObj.getDouble("priceChange");
                Double changePerc = cObj.getDouble("changePerc");

                // Create Stock and add to ArrayList
                Stock stock = new Stock(symbol,companyName,price,priceChange,changePerc);
                stockList.add(stock);
            }
            mAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "readJSONData: " + e.getMessage());
        }
    }

    private void writeJSONData() {
        try{
            FileOutputStream fos = getApplicationContext().openFileOutput("DataFile.json", Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for(Stock s: stockList) {
                writer.beginObject();
                writer.name("symbol").value(s.getStockSymbol());
                writer.name("companyName").value(s.getCompanyName());
                writer.name("price").value(s.getPrice());
                writer.name("priceChange").value(s.getPriceChange());
                writer.name("changePerc").value(s.getChangePercentage());
                writer.endObject();
            }
            writer.endArray();
            writer.close();

        }catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "writeJSONData: "+ e.getMessage());
        }
    }
}