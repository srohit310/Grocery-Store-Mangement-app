package com.stancorp.grocerystorev1.Classes;

import java.io.Serializable;

public class Items implements Serializable {
    public String ItemCode;
    public String name;
    public String Unit;
    public String Category;
    public String Brand;
    public Float Selling_Price;
    public String Buying_Price;
    public String Stock_Type;
    public Boolean Valid;
    public Boolean Imguri;
    public String ImgUriString;
    public String Reorder_Lvl;
    public String Excess_LvL;
    public Items(){

    }
    public Items(String ItemCode, String name, String Unit, String Category,
                 String Brand, String Selling_Price, String Buying_Price, String Stock_Type,
                 Boolean Valid, Boolean Imguri, String ImgUriString, String Reorder_Lvl, String Excess_Lvl){
        this.name = name;
        this.ItemCode = ItemCode;
        this.Unit = Unit;
        this.Category = Category;
        this.Brand = Brand;
        this.Selling_Price = Float.parseFloat(Selling_Price);
        this.Buying_Price = Buying_Price;
        this.Stock_Type = Stock_Type;
        this.Valid = Valid;
        this.Imguri = Imguri;
        this.ImgUriString = ImgUriString;
        this.Reorder_Lvl = Reorder_Lvl;
        this.Excess_LvL = Excess_Lvl;
    }

    public Items(Items item){
        this.name = item.name;
        this.ItemCode = item.ItemCode;
        this.Unit = item.Unit;
        this.Category = item.Category;
        this.Brand = item.Brand;
        this.Selling_Price = item.Selling_Price;
        this.Buying_Price = item.Buying_Price;
        this.Stock_Type = item.Stock_Type;
        this.Valid = item.Valid;
        this.Imguri = item.Imguri;
        this.ImgUriString = item.ImgUriString;
        this.Reorder_Lvl = item.Reorder_Lvl;
        this.Excess_LvL = item.Excess_LvL;
    }

}
