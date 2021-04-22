package com.stancorp.grocerystorev1.Classes;

import java.io.Serializable;

public class ItemStockInfo implements Serializable {

    public String ItemCode;
    public String name;
    public String Total_Price;
    public String Total_Balance_Quantity;
    public String Default_Reorder_Quantity;
    public String Selling_Price;
    public Boolean valid;

    public ItemStockInfo(){}

    public ItemStockInfo(ItemStockInfo stockInfo){
        this.ItemCode = stockInfo.ItemCode;
        this.name = stockInfo.name;
        this.Total_Price = stockInfo.Total_Price;
        this.Total_Balance_Quantity = stockInfo.Total_Balance_Quantity;
        this.Default_Reorder_Quantity = stockInfo.Default_Reorder_Quantity;
        this.Selling_Price = stockInfo.Selling_Price;
        this.valid = stockInfo.valid;
    }

    public ItemStockInfo(String ItemCode, String name, String Total_Price, String Total_Balance_Quantity,
            String Default_Reorder_Quantity, String Selling_Price,Boolean valid){
        this.ItemCode = ItemCode;
        this.name = name;
        this.Total_Price = Total_Price;
        this.Total_Balance_Quantity = Total_Balance_Quantity;
        this.Default_Reorder_Quantity = Default_Reorder_Quantity;
        this.Selling_Price = Selling_Price;
        this.valid = valid;
    }
}
