package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "product")
public class Product {

    @DatabaseField(id = true, unique = true, canBeNull = false)
    private String RFID;
    @DatabaseField(canBeNull = false)
    private String code;
    @DatabaseField()
    private Integer transactionId;

    public Product(){ }

    public Product(String RFID, String code){
        this.RFID = RFID;
        this.code = code;
        this.transactionId = null;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }
}
