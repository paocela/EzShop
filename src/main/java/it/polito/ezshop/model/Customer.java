package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "customers")
public class Customer implements it.polito.ezshop.data.Customer {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField()
    private String name;

    @DatabaseField(unique = true)
    private String card;

    @DatabaseField()
    private Integer points;

    Customer() { }

    public Customer(String name) {
        this.name = name;
        this.card = "";
        this.points = 0;
    }

    @Override
    public String getCustomerName() {
        return name;
    }

    @Override
    public void setCustomerName(String customerName) {
        this.name = customerName;
    }

    @Override
    public String getCustomerCard() {
        return card;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        this.card = customerCard;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPoints() {
        return points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points = points;
    }
}
