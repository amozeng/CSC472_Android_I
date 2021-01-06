package com.amozeng.a3_stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder {

    TextView symbol;
    TextView companyName;
    TextView price;
    TextView priceChange;
    //TextView changePerc;

    public StockViewHolder(View view) {
        super(view);
        symbol = view.findViewById(R.id.symbol);
        companyName = view.findViewById(R.id.company);
        price = view.findViewById(R.id.price);
        priceChange = view.findViewById(R.id.priceChange);
        //changePerc = view.findViewById(R.id.percent);
    }
}
