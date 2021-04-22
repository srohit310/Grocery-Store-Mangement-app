package com.stancorp.grocerystorev1.Classes;

public class LocationStockItem {
    public String ItemCode;
    public String LocationCode;
    public String Balance_Qty;
    public String Reorder_Qty;
    public Boolean valid;

    public LocationStockItem() {
    }

    public LocationStockItem(String Balance_qty, String Reorder_Qty, Boolean Valid, String ItemCode, String LocationCode) {
        this.ItemCode = ItemCode;
        this.LocationCode = LocationCode;
        this.Balance_Qty = Balance_qty;
        this.Reorder_Qty = Reorder_Qty;
        this.valid = Valid;
    }

}
