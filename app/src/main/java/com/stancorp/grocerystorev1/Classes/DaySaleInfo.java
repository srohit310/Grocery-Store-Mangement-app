package com.stancorp.grocerystorev1.Classes;

public class DaySaleInfo {
    public String date;
    public float dayProfit;
    public long noofPurchases, noofSales;

    public DaySaleInfo(){}

    public DaySaleInfo(String date, float dayProfit, long noofPurchases, long noofSales){
        this.date =date;
        this.dayProfit = dayProfit;
        this.noofPurchases = noofPurchases;
        this.noofSales = noofSales;
    }
}
