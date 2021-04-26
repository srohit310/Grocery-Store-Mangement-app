package com.stancorp.grocerystorev1.Classes;

import java.util.ArrayList;

public class StoreTransaction {
    public String code;
    public long codeno;
    public String type;
    public String reference;
    public String stakeholderCode;
    public String locationCode;
    public float totalPrice;
    public float totalProfit;
    public String madeByName;
    public String madeByEmail;
    public String madeByDate;
    public Boolean pending;
    public DeliveryAddress deliveryAddressfrom;
    public DeliveryAddress deliveryAddressto;

    public StoreTransaction(){ }

    public StoreTransaction(String code, long codeno, String type, String reference, String stakeholderCode, String locationCode, float totalPrice, float totalProfit,
                            String madeByName, String madeByEmail, String madeByDate, Boolean pending, DeliveryAddress deliveryAddressfrom,
                            DeliveryAddress deliveryAddressto){
        this.code = code;
        this.codeno = codeno;
        this.type = type;
        this.reference = reference;
        this.stakeholderCode = stakeholderCode;
        this.locationCode = locationCode;
        this.totalPrice = totalPrice;
        this.totalProfit = totalProfit;
        this.madeByName = madeByName;
        this.madeByEmail = madeByEmail;
        this.madeByDate = madeByDate;
        this.pending = pending;
        this.deliveryAddressfrom = deliveryAddressfrom;
        this.deliveryAddressto = deliveryAddressto;
    }


}
