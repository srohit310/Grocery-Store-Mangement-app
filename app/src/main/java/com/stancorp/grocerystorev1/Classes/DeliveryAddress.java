package com.stancorp.grocerystorev1.Classes;

public class DeliveryAddress {
    public String Street;
    public String City;
    public String State;
    public long Pincode;

    public DeliveryAddress(){}

    public DeliveryAddress(String street, String city,String state, long pincode){
        this.Street = street;
        this.State = state;
        this.City = city;
        this.Pincode = pincode;
    }
}
