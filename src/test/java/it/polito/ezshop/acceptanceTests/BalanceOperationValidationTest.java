package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.BalanceOperation;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class BalanceOperationValidationTest {

    @Test
    public void testSetGetBalanceId() {
        BalanceOperation balanceOperation = new BalanceOperation(50);
        balanceOperation.setBalanceId(1);
        assertEquals(1, balanceOperation.getBalanceId());
    }

    @Test
    public void testGetType() {
        BalanceOperation balanceOperation = new BalanceOperation(-50);
        assertEquals("DEBIT", balanceOperation.getType());
    }

    @Test
    public void testSetType() {
        BalanceOperation balanceOperation = new BalanceOperation();
        balanceOperation.setType("CREDIT");
        assertEquals("CREDIT", balanceOperation.getType());
    }

    @Test
    public void testSetGetMoney() {
        BalanceOperation balanceOperation = new BalanceOperation();
        balanceOperation.setMoney(50);
        assertEquals(50, balanceOperation.getMoney(), 0);
    }

    @Test
    public void testSetGetDate() {
        BalanceOperation balanceOperation = new BalanceOperation();
        balanceOperation.setDate(LocalDate.now());
        assertEquals(LocalDate.now(), balanceOperation.getDate());
    }

}
