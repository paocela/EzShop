package it.polito.ezshop.acceptanceTests;

import org.junit.Test;

import static org.junit.Assert.*;

public class barCodeValidationTest {

    @Test
    public void testValidBarCode(){
        String testBarCode = "0123456789012"; //13 digits
        assertTrue(it.polito.ezshop.data.EZShop.validateBarcode(testBarCode));
    }

    @Test
    public void testInvalidBarCode(){
        String testBarCode = "01234567890123"; //14 digits
        assertFalse(it.polito.ezshop.data.EZShop.validateBarcode(testBarCode));
    }

    @Test
    public void testOutOfBoundaryBarCode(){
        String testBarCode = "012345678"; //9 digits
        assertFalse(it.polito.ezshop.data.EZShop.validateBarcode(testBarCode));
    }

    @Test
    public void testNullBarCode(){
        //assertFalse(it.polito.ezshop.data.EZShop.validateBarcode(null));
        assertThrows(NullPointerException.class, () -> {it.polito.ezshop.data.EZShop.validateBarcode(null);} );
    }

}
