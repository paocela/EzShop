package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "credit_cards")
public class CreditCard {

    @DatabaseField(id = true)
    private String code;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private double amount;

    CreditCard(){}

    public CreditCard(String code, double amount){
        setCode(code);
        setAmount(amount);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
