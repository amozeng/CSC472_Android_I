package com.amozeng.a3_stockwatch;

import java.io.Serializable;
import java.util.Objects;

public class Stock implements Serializable, Comparable<Stock> {

    private String stockSymbol;
    private String companyName;
    private double price;
    private double priceChange;
    private double changePercent;

    public Stock(String symbol, String company, double p, double pC, double cP){
        this.stockSymbol = symbol;
        this.companyName = company;
        this.price = p;
        this.priceChange = pC;
        this.changePercent = cP;
    }

    public String getStockSymbol() { return this.stockSymbol; }
    public String getCompanyName() { return this.companyName;}
    public double getPrice() {return this.price;}
    public double getPriceChange() { return this.priceChange; }
    public double getChangePercentage() { return this.changePercent; }

    public void setPrice(Double p) {this.price = p;}
    public void setPriceChange(Double c) {this.priceChange = c;}
    public void setChangePercent(Double cP) {this.changePercent = cP;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(stockSymbol, stock.stockSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockSymbol);
    }

    @Override
    public int compareTo(Stock o) {
        return this.stockSymbol.compareTo(o.getStockSymbol());
    }
}
