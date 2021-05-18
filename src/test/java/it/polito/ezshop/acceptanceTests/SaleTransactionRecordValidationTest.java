package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.SaleTransactionRecord;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaleTransactionRecordValidationTest {
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

    @Test
    public void testSetId(){
        Integer testId = 42;

        SaleTransactionRecord record = new SaleTransactionRecord();
        record.setId(testId);

        assertEquals(testId, record.getId());
    }

    @Test
    public void testSetTotalPrice() {
        double testPrice = 42;

        SaleTransactionRecord record = new SaleTransactionRecord();
        record.setTotalPrice(testPrice);

        assertEquals(testPrice, record.getTotalPrice(), .01);
    }

    @Test
    public void testSetProductType() {
        ProductType testProduct = new ProductType("Nutella", "1", 3.2, "Any");

        SaleTransactionRecord record = new SaleTransactionRecord();
        record.setProductType(testProduct);

        assertEquals(testProduct.getProductDescription(), record.getProductDescription());
        assertEquals(testProduct.getBarCode(), record.getBarCode());
        assertEquals(testProduct.getPricePerUnit(), record.getPricePerUnit(), .01);
        assertEquals(testProduct.getNote(), record.getProductType().getNote());

    }

    @Test
    public void testSetBarCode() {
        ProductType testProduct = new ProductType("Nutella", "1", 3.2, "Any");
        String testBarcode = "123456789012";

        SaleTransactionRecord record = new SaleTransactionRecord();
        record.setProductType(testProduct);

        record.setBarCode(testBarcode);

        assertEquals(record.getBarCode(), testBarcode);
    }

    @Test
    public void testSetProductDescription() {
        ProductType testProduct = new ProductType("Nutella", "1", 3.2, "Any");
        String testDescription = "Nutella biscuits";

        SaleTransactionRecord record = new SaleTransactionRecord();
        record.setProductType(testProduct);

        record.setProductDescription(testDescription);

        assertEquals(record.getProductDescription(), testDescription);
    }

    @Test
    public void testSetAmount() {
        int testAmount = 42;

        SaleTransactionRecord record = new SaleTransactionRecord();
        record.setAmount(testAmount);

        assertEquals(record.getAmount(), testAmount);
    }

    @Test
    public void testSetPricePerUnit() {
        ProductType testProduct = new ProductType("Nutella", "1", 3.2, "Any");
        double testUnitPrice = 42.0;

        SaleTransactionRecord record = new SaleTransactionRecord();
        record.setProductType(testProduct);

        record.setPricePerUnit(testUnitPrice);

        assertEquals(record.getPricePerUnit(), testUnitPrice, .01);
    }

    @Test
    public void testSetDiscountRate() {
        double testDiscount = .5;

        SaleTransactionRecord record = new SaleTransactionRecord();
        record.setDiscountRate(testDiscount);

        assertEquals(record.getDiscountRate(), testDiscount, .01);
    }

}
