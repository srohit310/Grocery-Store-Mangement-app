package com.stancorp.grocerystorev1.Classes;

public class Itemtransaction {
    public String itemCode;
    public float Price;
    public float quantity;

    public Itemtransaction(){ }

    public Itemtransaction(String itemCode, float Price, float quantity){
        this.itemCode = itemCode;
        this.Price = Price;
        this.quantity = quantity;
    }

    public Itemtransaction(String itemCode, String Price, String quantity){
        this.itemCode = itemCode;
        this.Price = Float.parseFloat(Price);
        this.quantity = Float.parseFloat(quantity);
    }
}
