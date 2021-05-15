package it.polito.ezshop.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import it.polito.ezshop.data.TicketEntry;

import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private double discountRate = 0;

    @DatabaseField(columnName = "payment_type")
    private String paymentType;

    @DatabaseField()
    private double cash;

    @DatabaseField()
    private double change;

    @DatabaseField(columnName = "credit_card", foreign = true, foreignAutoRefresh = true)
    private CreditCard creditCard;

    @DatabaseField(canBeNull = false, columnName = "created_at")
    private final long createdAt = new Date().getTime();

    @ForeignCollectionField(eager = true)
    private ForeignCollection<SaleTransactionRecord> records;

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

    @Override
    public double getDiscountRate() {
        return discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
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

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
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
        // Unused method required by the teacher-defined interface
        // Might regret writing this later
        try {
            this.records = this.records.getDao().getEmptyForeignCollection("records");

            for (TicketEntry entry : entries) {
                SaleTransactionRecord record = new SaleTransactionRecord();

                record.setBarCode(entry.getBarCode());
                record.setProductDescription(entry.getProductDescription());
                record.setAmount(entry.getAmount());
                record.setPricePerUnit(entry.getPricePerUnit());
                record.setDiscountRate(entry.getDiscountRate());

                this.records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public double getPrice() {
        return getAmount();
    }

    @Override
    public void setPrice(double price) {
        setAmount(price);
    }

    public void refreshAmount() {
        double updatedAmount = this.records.stream().mapToDouble(SaleTransactionRecord::getTotalPrice).sum();

        this.amount = updatedAmount * (1 - discountRate);
    }

    public void setRecords(ForeignCollection<SaleTransactionRecord> records) {
        this.records = records;
    }


    public ForeignCollection<SaleTransactionRecord> getRecords() {
        return this.records;
    }

    public void updateSaleTransactionRecord(ProductType productType, int toaddquantity) {
        // TODO
        refreshAmount();
    }
}
