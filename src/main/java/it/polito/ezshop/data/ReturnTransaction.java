package it.polito.ezshop.data;

import java.util.List;

public interface ReturnTransaction {

    Integer getReturnId();

    void setReturnId(Integer id);

    Integer getTicketNumber();

    void setTicketNumber(Integer ticketNumber);

    double getReturnValue();

    void setReturnValue(double value);

    List<ReturnTransactionRecord> getRecords();

    //void setRecords(List<ReturnTransactionRecord> record);


}
