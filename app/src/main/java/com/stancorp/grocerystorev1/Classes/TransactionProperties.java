package com.stancorp.grocerystorev1.Classes;

public class TransactionProperties {
    public int pendingSales;
    public int pendingPurchases;

    public TransactionProperties() {
    }

    public TransactionProperties(int pendingPurchases,int pendingSales) {
        this.pendingSales = pendingSales;
        this.pendingPurchases = pendingPurchases;
    }
}
