package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import it.polito.ezshop.data.TicketEntry;

@DatabaseTable(tableName = "sale_transaction_records")
public class SaleTransactionRecord implements TicketEntry {
    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false)
    private int amount = 0;

    @DatabaseField(canBeNull = false, columnName = "total_price")
    private double totalPrice = 0;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "product_type_id", foreignAutoRefresh = true)
    private ProductType productType;

    @DatabaseField(canBeNull = false, columnName = "discount_rate")
    private double discountRate = 0;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "sale_transaction_id")
    private SaleTransaction saleTransaction;

    public SaleTransactionRecord() {
    }

    public SaleTransactionRecord(SaleTransaction saleTransaction, ProductType productType, int amount) {
        this.saleTransaction = saleTransaction;
        this.productType = productType;
        this.amount = amount;
        refreshTotalPrice();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void refreshTotalPrice() {
        this.totalPrice = productType.getPricePerUnit() * amount * (1 - discountRate);
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @Override
    public String getBarCode() {
        return productType.getBarCode();
    }

    @Override
    public void setBarCode(String barCode) {
        productType.setBarCode(barCode);
    }

    @Override
    public String getProductDescription() {
        return productType.getProductDescription();
    }

    @Override
    public void setProductDescription(String productDescription) {
        productType.setProductDescription(productDescription);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public double getPricePerUnit() {
        return productType.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        productType.setPricePerUnit(pricePerUnit);
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
