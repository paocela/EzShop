package it.polito.ezshop.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import it.polito.ezshop.data.TicketEntry;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@DatabaseTable(tableName = "sale_transactions")
public class SaleTransaction implements it.polito.ezshop.data.SaleTransaction {
    public enum StatusEnum {STARTED, CLOSED, PAID}

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false)
    private StatusEnum status = StatusEnum.STARTED;

    @DatabaseField(canBeNull = false)
    private double amount = 0;

    @DatabaseField(canBeNull = false, columnName = "discount_rate")
    private double discountRateAmount = 0;

    @DatabaseField(columnName = "payment_type")
    private String paymentType;

    @DatabaseField()
    private double cash;

    @DatabaseField()
    private double change;

    @DatabaseField(columnName = "credit_card")
    private String creditCard;

    @DatabaseField(canBeNull = false, columnName = "created_at")
    private final long createdAt = new Date().getTime();

    @ForeignCollectionField(eager = true)
    ForeignCollection<SaleTransactionRecord> records;

    public SaleTransaction() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Date getDate() {
        return new Date(createdAt);
    }

    public LocalTime getTime() {
        return LocalDateTime.ofEpochSecond(createdAt, 0, ZoneOffset.of("GMT+2"))
                .toLocalTime();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getDiscountRateAmount() {
        return discountRateAmount;
    }

    public void setDiscountRateAmount(double discountRateAmount) {
        this.discountRateAmount = discountRateAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    @Override
    public Integer getTicketNumber() {
        return getId();
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        setId(ticketNumber);
    }

    @Override
    public List<TicketEntry> getEntries() {
        return new ArrayList<>(this.records);
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {

    }

    @Override
    public double getDiscountRate() {
        return getDiscountRateAmount();
    }

    @Override
    public void setDiscountRate(double discountRate) {
        setDiscountRateAmount(discountRate);
    }

    @Override
    public double getPrice() {
        return getAmount();
    }

    @Override
    public void setPrice(double price) {
        setAmount(price);
    }

    public boolean addSaleTransactionRecord(ProductType product, int quantity) throws SQLException {
        // First check for an existing record for this product
        SaleTransactionRecord transactionRecord = this.records.getDao().queryBuilder()
                .where().eq("product_type_id", product.getId()).and()
                .eq("sale_transaction_id", this.id).queryForFirst();

        if (transactionRecord == null) {
            // No existing record for this product, creating a new one
            transactionRecord = new SaleTransactionRecord(product, quantity);
            this.records.add(transactionRecord);
        } else {
            // There is an existing record for this product, increasing quantity
            transactionRecord.setAmount(transactionRecord.getAmount() + quantity);
            transactionRecord.setTotalPrice(transactionRecord.getTotalPrice() + transactionRecord.getPricePerUnit() * quantity);

            this.records.update(transactionRecord);
        }

        return true;
    }

    public void refreshAmount() {
        double updatedAmount = this.records.stream().mapToDouble(SaleTransactionRecord::getTotalPrice).sum();
        System.out.println("Updated amount is " + updatedAmount);

        this.amount = Math.round(updatedAmount * (1 - discountRateAmount) * 100.0) / 100.0;
    }
}
