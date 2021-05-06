package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.exceptions.InvalidCreditCardException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import it.polito.ezshop.data.EZShop;


public class TestJUnit1 {

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
}
