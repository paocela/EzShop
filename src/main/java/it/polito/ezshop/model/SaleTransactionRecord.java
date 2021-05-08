package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import it.polito.ezshop.data.TicketEntry;

@DatabaseTable(tableName = "sale_transaction_records")
public class SaleTransactionRecord implements TicketEntry {
    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false)
    private int quantity = 0;

    @DatabaseField(canBeNull = false, columnName = "total_price")
    private double totalPrice = 0;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "product_type_id")
    private ProductType productType;

    @DatabaseField(canBeNull = false, columnName = "discount_rate")
    private double discountRate = 0;

    @DatabaseField(canBeNull = false, foreign = true)
    private SaleTransaction saleTransaction;

    SaleTransactionRecord() {
    }

    public SaleTransactionRecord(ProductType productType, int quantity) {
        setProductType(productType);
        setQuantity(quantity);
        setTotalPrice(productType.getSalePrice() * quantity);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @Override
    public String getBarCode() {
        return null;
    }

    @Override
    public void setBarCode(String barCode) {
        // TODO WHAT THE FUCK SHOULD THIS DO?!
    }

    @Override
    public String getProductDescription() {
        return productType.getProductDescription();
    }

    @Override
    public void setProductDescription(String productDescription) {
        // TODO WHAT THE FUCK SHOULD THIS DO?!
    }

    @Override
    public int getAmount() {
        return getQuantity();
    }

    @Override
    public void setAmount(int amount) {
        setQuantity(amount);
    }

    @Override
    public double getPricePerUnit() {
        return productType.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        // TODO WHAT THE FUCK SHOULD THIS DO?!
    }

    @Override
    public double getDiscountRate() {
        return discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
