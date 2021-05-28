package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.Customer;
import org.junit.Test;

import static org.junit.Assert.*;

public class CustomerValidationTest {
    @Test
    public void testGetCustomerName() {
        Customer customer = new Customer("Marco");
        assertEquals("Marco", customer.getCustomerName());
    }

    @Test
    public void testSetCustomerName() {
        Customer customer = new Customer("Marco");
        customer.setCustomerName("Sara");
        assertEquals("Sara", customer.getCustomerName());
    }

    @Test
    public void testGetCustomerCard() {
        Customer customer = new Customer("Marco");
        assertNull(customer.getCustomerCard());
    }

    @Test
    public void testSetCustomerCard() {
        Customer customer = new Customer("Marco");
        customer.setCustomerCard("5046981164");
        assertEquals("5046981164", customer.getCustomerCard());
    }

    @Test
    public void testGetId() {
        Customer customer = new Customer("Marco");
        assertNull(customer.getId());
    }

    @Test
    public void testSetId() {
        Customer customer = new Customer("Marco");
        customer.setId(20);
        assertEquals(20, customer.getId(), 0);
    }

    @Test
    public void testGetPoints() {
        Customer customer = new Customer("Marco");
        assertEquals(0, customer.getPoints(), 0);
    }

    @Test
    public void testSetPoints() {
        Customer customer = new Customer("Marco");
        customer.setPoints(20);
        assertEquals(20, customer.getPoints(), 0);
    }

    @Test
    public void testCustomer() {
        Customer customer = new Customer("Marco");
        assertEquals("Marco", customer.getCustomerName());
        assertNull(customer.getCustomerCard());
        assertEquals(0, customer.getPoints(), 0);
    }

    @Test
    public void testEmptyCustomer() {
        Customer customer = new Customer();
        assertNull(customer.getCustomerName());
        assertNull(customer.getCustomerCard());
        assertNull(customer.getId());
        assertNull(customer.getPoints());
    }
}
