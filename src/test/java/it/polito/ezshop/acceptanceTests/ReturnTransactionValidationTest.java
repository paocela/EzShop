package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.ReturnTransaction;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class ReturnTransactionValidationTest {

    @Test
    public void testSetReturnId() {
        Integer testReturnId = 36;

        ReturnTransaction returnTransaction = new ReturnTransaction();
        returnTransaction.setReturnId(testReturnId);

        assertEquals(testReturnId, returnTransaction.getReturnId());
    }

    @Test
    public void testSetTicketNumber() {
        Integer testTicketNumber = 4;

        ReturnTransaction returnTransaction = new ReturnTransaction();
        returnTransaction.setTicketNumber(testTicketNumber);

        assertEquals(testTicketNumber, returnTransaction.getTicketNumber());
    }

    @Test
    public void testSetStatus() {
        ReturnTransaction.StatusEnum statusEnum = ReturnTransaction.StatusEnum.CLOSED;

        ReturnTransaction returnTransaction = new ReturnTransaction();
        returnTransaction.setStatus(statusEnum);

        assertEquals(statusEnum, returnTransaction.getStatus());
    }

    @Test
    public void testSetReturnValue() {
        double testReturnValue = 42.56;

        ReturnTransaction returnTransaction = new ReturnTransaction();
        returnTransaction.setReturnValue(testReturnValue);

        assertEquals(testReturnValue, returnTransaction.getReturnValue(), .01);

    }

}
