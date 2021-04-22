package com.stancorp.grocerystorev1.Classes;

public class maxindex {
    public long agentCode;
    public long locationCode;
    public long purchaseCode;
    public long salesCode;
    public long customerCode;
    public long vendorCode;

    public maxindex(){}

    public maxindex(long agentCode,long locationCode,long purchaseCode,long salesCode,long customerCode,long vendorCode){
        this.agentCode = agentCode;
        this.locationCode = locationCode;
        this.purchaseCode = purchaseCode;
        this.salesCode = salesCode;
        this.customerCode = customerCode;
        this.vendorCode = vendorCode;
    }
}
