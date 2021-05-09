package it.polito.ezshop.data;

public interface ReturnTransactionRecord {

    Integer getRecordId();

    void setRecordId(Integer id);

    String getProductId();

    void setProductId(String id);

    int getQuantity();

    void setQuantity(int quantity);

    double getTotalPrice();

    void setTotalPrice(double amount);

}
