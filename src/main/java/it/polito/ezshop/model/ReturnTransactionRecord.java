package it.polito.ezshop.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "return_transaction_records")
public class ReturnTransactionRecord implements it.polito.ezshop.data.ReturnTransactionRecord {

    @DatabaseField(generatedId = true)
    private Integer recordId;

    @DatabaseField(canBeNull = false)
    private String productId;

    @DatabaseField(canBeNull = false)
    private int quantity;

    @DatabaseField(canBeNull = false)
    private double totalPrice;

    public ReturnTransactionRecord() {
    }

    public ReturnTransactionRecord(String productId, int quantity, double totalPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    @Override
    public Integer getRecordId() {
        return recordId;
    }

    @Override
    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
