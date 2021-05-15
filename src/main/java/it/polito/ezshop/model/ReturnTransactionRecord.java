package it.polito.ezshop.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import it.polito.ezshop.data.ReturnEntry;


@DatabaseTable(tableName = "return_transaction_records")
public class ReturnTransactionRecord implements ReturnEntry {

    @DatabaseField(generatedId = true)
    private Integer recordId;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "product_type_id", foreignAutoRefresh = true)
    private ProductType productType;

    @DatabaseField(canBeNull = false)
    private int quantity;

    @DatabaseField(canBeNull = false)
    private double totalPrice;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "return_transaction_id")
    private ReturnTransaction returnTransaction;

    public ReturnTransactionRecord() {
    }

    public ReturnTransactionRecord(ProductType productType, int quantity, double totalPrice) {
        this.productType = productType;
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
    public ProductType getProductType() {
        return productType;
    }

    @Override
    public void setProductType(ProductType productType) {
        this.productType = productType;
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
