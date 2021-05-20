package it.polito.ezshop.data;

import it.polito.ezshop.model.ProductType;

public interface ReturnEntry {

    Integer getRecordId();

    void setRecordId(Integer id);

    public it.polito.ezshop.model.ProductType getProductType();

    public void setProductType(ProductType productType);

    int getQuantity();

    void setQuantity(int quantity);

    double getTotalPrice();

    void setTotalPrice(double amount);

}
