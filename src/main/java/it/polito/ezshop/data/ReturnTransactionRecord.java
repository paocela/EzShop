package it.polito.ezshop.data;

public interface ReturnTransactionRecord {

    Integer getrecordId();

    void setrecordId(Integer id);

    Integer getproductId();

    void setproductId(Integer id);

    int getQuantity();

    void setQuantity(int amount);

    int getTotalPrice();

    void setTotalPrice(int amount);

}
