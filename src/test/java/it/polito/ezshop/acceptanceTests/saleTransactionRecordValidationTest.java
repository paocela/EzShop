package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.SaleTransactionRecord;
import org.junit.Test;
import static org.junit.Assert.*;

public class saleTransactionRecordValidationTest {
    @Test
    public void testValidFormula() {
        ProductType productType = new ProductType("Nutella", "1", 3.2, "");
        SaleTransactionRecord saleTransactionRecord = new SaleTransactionRecord(null, productType, 10);

        assertEquals(32, saleTransactionRecord.getTotalPrice(), 0);

        saleTransactionRecord.setDiscountRate(0.2);

        saleTransactionRecord.refreshTotalPrice();

        assertEquals(25.6, saleTransactionRecord.getTotalPrice(), 0);

        saleTransactionRecord.setAmount(20);

        saleTransactionRecord.refreshTotalPrice();

        assertEquals(51.2, saleTransactionRecord.getTotalPrice(), 0);


    }
}
