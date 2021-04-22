package com.stancorp.grocerystorev1.Classes;

public class Location {
    public String code;
    public String name;
    public DeliveryAddress address;
    public long codeno;

    public Location(){}

    public Location(String code, String name, DeliveryAddress address, long codeno){
        this.code = code;
        this.name = name;
        this.address = address;
        this.codeno = codeno;
    }

    @Override
    public String toString() {
        return this.name.toLowerCase();
    }
}
