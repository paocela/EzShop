package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.Order;
import org.junit.Test;
import static org.junit.Assert.*;

public class orderTest {

    @Test
    public void testSetGetOrderId() {
        Order order = new Order("1", 5, 3.0);
        order.setOrderId(1);
        assertEquals(1, order.getOrderId(), 0);
    }

    @Test
    public void testGetSetBalanceId() {
        Order order = new Order();
        order.setBalanceId(1);
        assertEquals(1, order.getBalanceId(), 0);
    }

    @Test
    public void testGetSetProductCode() {
        Order order = new Order();
        order.setProductCode("1");
        assertEquals("1", order.getProductCode());
    }

    @Test
    public void testGetSetQuantity() {
        Order order = new Order();
        order.setQuantity(5);
        assertEquals(5, order.getQuantity(), 0);
    }

    @Test
    public void testGetSetPricePerUnit() {
        Order order = new Order();
        order.setPricePerUnit(3);
        assertEquals(3, order.getPricePerUnit(), 0);
    }

    @Test
    public void testGetSetStatus() {
        Order order = new Order();
        order.setStatus("ISSUED");
        assertEquals("ISSUED", order.getStatus());

        //test change of status
        order.setStatus("PAYED");
        assertEquals("PAYED", order.getStatus());

        order.setStatus("COMPLETED");
        assertEquals("COMPLETED", order.getStatus());
    }

}
