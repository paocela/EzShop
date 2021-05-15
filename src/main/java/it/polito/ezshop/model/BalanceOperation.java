package it.polito.ezshop.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "balance_operations")
public class BalanceOperation implements it.polito.ezshop.data.BalanceOperation {

    public enum TypeEnum {CREDIT, DEBIT}

    @DatabaseField(generatedId = true)
    private int balanceId;

    @DatabaseField(canBeNull = false, columnName = "money")
    private double money;

    @DatabaseField(canBeNull = false)
    private TypeEnum type;

    @DatabaseField(canBeNull = false, columnName = "date_string")
    private String dateString;

    public BalanceOperation() {
    }

    public BalanceOperation(double money) {
        this.money = money;
        if (Double.doubleToRawLongBits(money) < 0) {
            this.type = TypeEnum.DEBIT;
        } else {
            this.type = TypeEnum.CREDIT;
        }
        this.dateString = LocalDate.now().toString();
    }

    @Override
    public int getBalanceId() {
        return balanceId;
    }

    @Override
    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public LocalDate getDate() {
        return LocalDate.parse(dateString);
    }

    @Override
    public void setDate(LocalDate date) {
        this.dateString = date.toString();
    }

    @Override
    public double getMoney() {
        return money;
    }

    @Override
    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public String getType() {
        return type.toString();
    }

    @Override
    public void setType(String type) {
        this.type = TypeEnum.valueOf(type);
    }
}
