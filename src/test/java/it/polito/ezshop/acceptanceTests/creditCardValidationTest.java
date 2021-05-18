package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.exceptions.InvalidCreditCardException;
import org.junit.Assert;
import org.junit.Test;

import it.polito.ezshop.data.EZShop;

import static org.junit.Assert.*;


public class creditCardValidationTest {

    @Test
    public void testCorrectCreditCard() {
        String creditCard = "12345674";
        assertTrue(it.polito.ezshop.data.EZShop.validateCreditCard(creditCard));
    }

    @Test
    public void testWrongCreditCard()  {
        String creditCard = "13245674";
        assertFalse(it.polito.ezshop.data.EZShop.validateCreditCard(creditCard));
    }

    @Test
    public void testNonNumberCreditCard()  {
        String creditCard = "123C";
        assertFalse(it.polito.ezshop.data.EZShop.validateCreditCard(creditCard));
    }

}
