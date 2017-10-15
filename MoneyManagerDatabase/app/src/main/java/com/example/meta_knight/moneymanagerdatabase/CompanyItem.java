package com.example.meta_knight.moneymanagerdatabase;

public class CompanyItem {
    private String CompName, Owner;

    public CompanyItem() { }

    public CompanyItem(String inCompName, String inOwner) {
        this.CompName = inCompName;
        this.Owner = inOwner;
    }

    public String getCompName() {
        return this.CompName;
    }

    public String getOwner() {
        return this.Owner;
    }

    public void setCompName(String inTempStr) {
        this.CompName = inTempStr;
    }

    public void setOwner(String inTempStrOwner) {
        this.Owner = inTempStrOwner;
    }
}
