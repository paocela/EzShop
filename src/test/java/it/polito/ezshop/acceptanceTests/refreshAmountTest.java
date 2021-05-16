package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.SaleTransaction;
import it.polito.ezshop.model.SaleTransactionRecord;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class refreshAmountTest {
    @Test
    public void testRefreshAmount() {
        ProductType productType = new ProductType("Nutella", "1", 3.2, "");
        SaleTransaction saleTransaction = new SaleTransaction();
        SaleTransactionRecord saleTransactionRecord = new SaleTransactionRecord(saleTransaction, productType, 10);
        List<TicketEntry> entries = new ArrayList<>();
        entries.add(saleTransactionRecord);
        saleTransaction.setEntries(entries);

        assertEquals(32, saleTransaction.getAmount(), 0);

        saleTransaction.setDiscountRate(0.2);

        saleTransaction.refreshAmount();

        assertEquals(25.6, saleTransaction.getAmount(), 0);

        saleTransaction.getEntries().get(0).setAmount(20);

        saleTransaction.refreshAmount();

        assertEquals(51.2, saleTransaction.getAmount(), 0);

    }
}
