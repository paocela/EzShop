package it.polito.ezshop.acceptanceTests;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class barCodeValidationTest {

    @Test
    public void testCorrectBarCode(){
        String testBarCode = "0123456789012"; //13 digits
        assertTrue(it.polito.ezshop.data.EZShop.validateBarcode(testBarCode));
    }

    @Test
    public void testWrongBarCode(){
        String testBarCode = "01234567890123"; //14 digits
        assertFalse(it.polito.ezshop.data.EZShop.validateBarcode(testBarCode));
    }

    @Test
    public void testTooShortBarCode(){
        String testBarCode = "012345678"; //9 digits
        assertFalse(it.polito.ezshop.data.EZShop.validateBarcode(testBarCode));
    }

    @Test
    public void testTooLongBarCode(){
        String testBarCode = "0123456789012345"; //16 digits
        assertFalse(it.polito.ezshop.data.EZShop.validateBarcode(testBarCode));
    }
}
