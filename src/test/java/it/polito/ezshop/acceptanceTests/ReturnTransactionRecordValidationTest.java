package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.ReturnTransactionRecord;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReturnTransactionRecordValidationTest {

    @Test
    public void testSetRecordId() {
        Integer testReturnId = 36;

        ReturnTransactionRecord returnTransactionRecord = new ReturnTransactionRecord();
        returnTransactionRecord.setRecordId(testReturnId);

        assertEquals(testReturnId, returnTransactionRecord.getRecordId());
    }

    @Test
    public void testSetProductType() {
        ProductType testProduct = new ProductType("Acqua", "2", 2.2, "Any");

        ReturnTransactionRecord record = new ReturnTransactionRecord();
        record.setProductType(testProduct);

        assertEquals(testProduct.getProductDescription(), record.getProductType().getProductDescription());
        assertEquals(testProduct.getBarCode(), record.getProductType().getBarCode());
        assertEquals(testProduct.getPricePerUnit(), record.getProductType().getPricePerUnit(), .01);
        assertEquals(testProduct.getNote(), record.getProductType().getNote());

    }


    @Test
    public void testSetTotalPrice() {
        double testPrice = 42;

        ReturnTransactionRecord record = new ReturnTransactionRecord();
        record.setTotalPrice(testPrice);

        assertEquals(testPrice, record.getTotalPrice(), .01);
    }

    @Test
    public void testSetQuantity() {
        int testQuantity = 42;

        ReturnTransactionRecord record = new ReturnTransactionRecord();
        record.setQuantity(testQuantity);

        assertEquals(record.getQuantity(), testQuantity);
    }


}
