package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "orders")
public class Order implements it.polito.ezshop.data.Order {

    public enum StatusEnum {ISSUED, PAYED, COMPLETED}

    @DatabaseField(generatedId = true)
    private Integer orderId;

    @DatabaseField(canBeNull = false)
    private Order.StatusEnum status = Order.StatusEnum.ISSUED;

    @DatabaseField(canBeNull = false)
    private String productCode;

    @DatabaseField()
    private Integer quantity;

    @DatabaseField()
    private Double pricePerUnit;


    @DatabaseField(canBeNull = false)
    private Integer balanceId=-1; //TODO: to fix after adding BalanceOperation


    public Order(){ }

    public Order(String productCode, Integer quantity, Double pricePerUnit){
        this.productCode = productCode;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public Integer getBalanceId() {
        return balanceId;
    }

    @Override
    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public String getProductCode() {
        return productCode;
    }

    @Override
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Override
    public double getPricePerUnit() {
        return pricePerUnit;
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
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
    public String getStatus() {
        return status.toString();
    }

    @Override
    public void setStatus(String status) {
        this.status = StatusEnum.valueOf(status);
    }

    @Override
    public Integer getOrderId() {
        return orderId;
    }

    @Override
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
