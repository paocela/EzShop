package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.model.CreditCard;
import it.polito.ezshop.model.SaleTransaction;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class SaleTransactionValidationTest {

    @Test
    public void testSetId() {
        Integer testId = 42;

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setId(testId);

        assertEquals(testId, saleTransaction.getId());
    }

    @Test
    public void testSetStatus() {
        SaleTransaction.StatusEnum statusEnum = SaleTransaction.StatusEnum.CLOSED;

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setStatus(statusEnum);

        assertEquals(statusEnum, saleTransaction.getStatus());
    }

    @Test
    public void testSetAmount() {
        double testAmount = 42;

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setAmount(testAmount);

        assertEquals(testAmount, saleTransaction.getAmount(), .01);

    }


    @Test
    public void testSetDiscountRate() {
        double testDiscountRate = .5;

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setDiscountRate(testDiscountRate);

        assertEquals(testDiscountRate, saleTransaction.getDiscountRate(), .01);

    }

    @Test
    public void testSetPaymentType() {
        String testPaymentType = "cash";

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setPaymentType(testPaymentType);

        assertEquals(testPaymentType, saleTransaction.getPaymentType());

    }

    @Test
    public void testSetCash() {
        double testCash = 42;

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setCash(testCash);


        assertEquals(testCash, saleTransaction.getCash(), .01);
    }

    @Test
    public void testSetChange() {
        double testChange = 42;

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setChange(testChange);

        assertEquals(testChange, saleTransaction.getChange(), .01);

    }

    @Test
    public void testSetCreditCard() {
        String testCardCode = "4242424242424242";
        double testCardAmount = 42;

        CreditCard testCard = new CreditCard(testCardCode, testCardAmount);

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setCreditCard(testCard);

        CreditCard retrievedCreditCard = saleTransaction.getCreditCard();

        assertEquals(testCardCode, retrievedCreditCard.getCode());
        assertEquals(testCardAmount, retrievedCreditCard.getAmount(), .01);

    }


    @Test
    public void testSetTicketNumber() {
        Integer testId = 42;

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setTicketNumber(testId);

        assertEquals(testId, saleTransaction.getTicketNumber());
    }


    @Test
    public void testSetEntries() {
        List<TicketEntry> testEntries = new ArrayList<>();

        SaleTransaction saleTransaction = new SaleTransaction();

        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
            saleTransaction.setEntries(testEntries);
        });
    }


    @Test
    public void testSetPrice() {
        double testPrice = 42;

        SaleTransaction saleTransaction = new SaleTransaction();
        saleTransaction.setPrice(testPrice);

        assertEquals(testPrice, saleTransaction.getPrice(), .01);
    }

    @Test
    public void testCreationDate(){
        Date testCreationDate = new Date();
        SaleTransaction saleTransaction = new SaleTransaction();

        assertEquals(testCreationDate.getTime(),saleTransaction.getDate().getTime(), 100);
    }

    @Test
    public void testCreationTime(){
        LocalTime testCreationTime = LocalDateTime.now().toLocalTime();

        SaleTransaction saleTransaction = new SaleTransaction();

        assertEquals(testCreationTime.toSecondOfDay(), saleTransaction.getTime().toSecondOfDay());
    }
}
