package com.stancorp.grocerystorev1.Classes;

import java.util.ArrayList;

public class TransactionItemList {
    public ArrayList<Itemtransaction> itemtransactions;

    public TransactionItemList(){}

    public TransactionItemList(ArrayList<Itemtransaction> itemtransactions){
        this.itemtransactions = itemtransactions;
    }
}
