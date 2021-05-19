package it.polito.ezshop.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import it.polito.ezshop.data.ReturnEntry;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "return_transactions")
public class ReturnTransaction implements it.polito.ezshop.data.ReturnTransaction {

    public enum StatusEnum {STARTED, CLOSED, PAID};

    @DatabaseField(generatedId = true)
    private Integer returnid;

    @DatabaseField(canBeNull = false)
    private Integer ticketNumber;

    @DatabaseField(canBeNull = false)
    private ReturnTransaction.StatusEnum status = ReturnTransaction.StatusEnum.STARTED;

    @DatabaseField(canBeNull = false)
    private double returnValue = 0;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<ReturnTransactionRecord> records;


    public ReturnTransaction() {
    }

    public ReturnTransaction(Integer ticketNumber) {
        this.ticketNumber = ticketNumber;
    }


    @Override
    public Integer getReturnId(){
        return returnid;
    }

    @Override
    public void setReturnId (Integer returnid) {
        this.returnid = returnid;
    }

    @Override
    public Integer getTicketNumber(){
        return ticketNumber;
    }

    @Override
    public void setTicketNumber(Integer ticketNumber){
        this.ticketNumber = ticketNumber;
    }


    public ReturnTransaction.StatusEnum getStatus() {
        return status;
    }

    public void setStatus(ReturnTransaction.StatusEnum status) {
        this.status = status;
    }


    @Override
    public double getReturnValue() {
        return returnValue;
    }

    @Override
    public void setReturnValue(double returnValue) {
        this.returnValue = returnValue;
    }

    public ForeignCollection<ReturnTransactionRecord> getReturnRecords() {
        return this.records;
    }

    /* @Override
    public void setRecords(List<ReturnTransactionRecord> records) {

    }
    */

}
