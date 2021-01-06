package com.amozeng.a3_stockwatch;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainActivity;
    
    public StockAdapter(List<Stock> empList, MainActivity _mainActivity) {
        this.stockList = empList;
        this.mainActivity = _mainActivity;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Making new ViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_entry, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getStockSymbol());
        holder.companyName.setText(stock.getCompanyName());
        holder.price.setText(String.format(Locale.getDefault(),"%.2f", stock.getPrice()));
        Double priceChange = stock.getPriceChange();
        Double pricePerc = stock.getChangePercentage();
        String priceChangeString = String.format(Locale.getDefault(), "%.2f", priceChange);
        String pricePercString = String.format(Locale.getDefault(), "%.2f", pricePerc);
        if(priceChange < 0) {
            holder.priceChange.setText("▼ "+ priceChangeString + "(" + pricePercString + "%)");
            holder.priceChange.setTextColor(Color.RED);
            holder.symbol.setTextColor(Color.RED);
            holder.companyName.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
        }else{
            holder.priceChange.setText("▲ "+ priceChangeString + "(" + pricePercString + "%)");
            holder.priceChange.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.symbol.setTextColor(Color.GREEN);
            holder.companyName.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
