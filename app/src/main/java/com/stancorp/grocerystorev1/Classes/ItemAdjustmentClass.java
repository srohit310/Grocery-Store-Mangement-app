package com.stancorp.grocerystorev1.Classes;

public class ItemAdjustmentClass {
    public String mode;
    public String ItemCode;
    public String LocationCode;
    public String Date;
    public String Reason;
    public String AmountAdjusted;
    public String Updatedby;

    public ItemAdjustmentClass(){ }

    public ItemAdjustmentClass(String mode,String ItemCode, String LocationCode,String Date,String Reason,
                               String Amount, String Updatedby){
        this.mode = mode;
        this.ItemCode = ItemCode;
        this.LocationCode = LocationCode;
        this.Date = Date;
        this.Reason = Reason;
        this.AmountAdjusted = Amount;
        this.Updatedby = Updatedby;
    }
}
