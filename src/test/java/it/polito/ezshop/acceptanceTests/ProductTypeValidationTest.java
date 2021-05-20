package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.ProductType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductTypeValidationTest {

    @Test
    public void testSetId() {
        Integer testId = 45;

        ProductType product = new ProductType();
        product.setId(testId);

        assertEquals(testId, product.getId());
    }

    @Test
    public void testSetBarCode() {
        String testBarCode = "0123456789012";

        ProductType product = new ProductType();
        product.setBarCode(testBarCode);

        assertEquals(testBarCode, product.getBarCode());
    }

    @Test
    public void testSetDescription() {
        String testDescription = "Vasetto nutella 440ml";

        ProductType product = new ProductType();
        product.setProductDescription(testDescription);

        assertEquals(testDescription, product.getProductDescription());
    }

    @Test
    public void testSetQuantity() {
        Integer testQuantity = 45;

        ProductType product = new ProductType();
        product.setQuantity(testQuantity);

        assertEquals(testQuantity, product.getQuantity());
    }

    @Test
    public void testSetNote() {
        String testNote = "Ingredienti: ...";

        ProductType product = new ProductType();
        product.setNote(testNote);

        assertEquals(testNote, product.getNote());
    }

    @Test
    public void testLocation() {
        String testLocation = "56-F45-89";

        ProductType product = new ProductType();
        product.setLocation(testLocation);

        assertEquals(testLocation, product.getLocation());
    }

    @Test
    public void testPricePerUnit() {
        Double testPriceperUnit = 4.50;

        ProductType product = new ProductType();
        product.setPricePerUnit(testPriceperUnit);

        assertEquals(testPriceperUnit, product.getPricePerUnit());
    }


}
