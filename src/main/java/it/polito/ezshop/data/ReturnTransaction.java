package it.polito.ezshop.data;

import java.util.List;

public interface ReturnTransaction {

    Integer getreturnId();

    void setreturnId(Integer id);

    Integer getTicketNumber();

    void setTicketNumber(Integer ticketNumber);

    double getreturnValue();

    void setreturnValue(double value);

    List<ReturnTransactionRecord> getEntries();

    void setEntries(List<ReturnTransactionRecord> entries);


}
