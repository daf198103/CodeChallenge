package model;

import java.util.Objects;


public class Asset {


    private String symbol;
    private Double priceUsd;


    public Asset(String symbol, Double priceUsd) {
        this.symbol = symbol;
        this.priceUsd = priceUsd;
    }

    public Asset(){}

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPriceUsd() {
        return priceUsd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;
        Asset asset = (Asset) o;
        return Objects.equals(symbol, asset.symbol) &&
                Objects.equals(priceUsd, asset.priceUsd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, priceUsd);
    }

    public void setPriceUsd(Double priceUsd) {
        this.priceUsd = priceUsd;
    }
}
