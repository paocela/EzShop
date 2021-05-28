package it.polito.ezshop.integrationTests;

import it.polito.ezshop.data.Customer;
import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CustomerIntegrationTest extends BaseIntegrationTest {

    static Integer customerId;
    static Integer customerId2;
    static Integer newCustomerId;
    static String customerCard;

    @BeforeClass
    public static void init() throws InvalidCustomerNameException, UnauthorizedException {
        loginAs(User.RoleEnum.ShopManager);
        customerId = shop.defineCustomer("validCustomerName");
        customerCard = shop.createCard();
        assertNotNull(customerCard);
    }

    @Test
    public void testCreateValidCustomer() {
        assertTrue(customerId > 0);
    }

    @Test
    public void testCreateCustomerUnauthorized() {
        shop.logout();

        assertThrows(UnauthorizedException.class,() -> shop.defineCustomer("validCustomerName"));
    }

    @Test
    public void testCreateCard() {
        loginAs(User.RoleEnum.ShopManager);
        assertNotNull(customerCard);
    }

    @Test
    public void testGetCustomer() throws UnauthorizedException, InvalidCustomerIdException {
        loginAs(User.RoleEnum.ShopManager);

        assertEquals(customerId, shop.getCustomer(customerId).getId());
    }

    @Test
    public void testGetAllCustomers() throws InvalidCustomerNameException, UnauthorizedException {
        loginAs(User.RoleEnum.ShopManager);

        customerId2 = shop.defineCustomer("newCustomerName");
        assertTrue(customerId2 > 0);
        List<Customer> list = shop.getAllCustomers();
        assertTrue(list.stream().map(x -> x.getId()).collect(Collectors.toList()).contains(customerId));
        assertTrue(list.stream().map(x -> x.getId()).collect(Collectors.toList()).contains(customerId2));
    }

    @Test
    public void testAttachCardToCustomer() throws UnauthorizedException, InvalidCustomerIdException, InvalidCustomerCardException {
        loginAs(User.RoleEnum.ShopManager);

        shop.attachCardToCustomer(customerCard, customerId);

        assertEquals(customerCard, shop.getCustomer(customerId).getCustomerCard());
    }

    @Test
    public void testInvalidCustomerCard() throws InvalidCustomerNameException, UnauthorizedException {
        loginAs(User.RoleEnum.ShopManager);

        newCustomerId = shop.defineCustomer("newCustomer");
        assertThrows(InvalidCustomerCardException.class, () -> shop.attachCardToCustomer("101", newCustomerId));

    }

    @Test
    public void testModifyValidNameCustomerRecord() throws InvalidCustomerNameException, UnauthorizedException, InvalidCustomerIdException, InvalidCustomerCardException {
        loginAs(User.RoleEnum.ShopManager);

        shop.modifyCustomer(customerId, "NewCustomerName", null);

        assertEquals("NewCustomerName", shop.getCustomer(customerId).getCustomerName());
    }

    @Test
    public void testModifyValidCardCustomerRecord() throws InvalidCustomerNameException, UnauthorizedException, InvalidCustomerIdException, InvalidCustomerCardException {
        loginAs(User.RoleEnum.ShopManager);

        String newCustomerCard = shop.createCard();
        shop.modifyCustomer(customerId, "validCustomerName", newCustomerCard);

        assertEquals(newCustomerCard, shop.getCustomer(customerId).getCustomerCard());
    }

    @Test
    public void testDetachValidCardFromCustomer() throws UnauthorizedException, InvalidCustomerNameException, InvalidCustomerIdException, InvalidCustomerCardException {
        loginAs(User.RoleEnum.ShopManager);

        shop.attachCardToCustomer(customerCard, customerId);
        shop.modifyCustomer(customerId, shop.getCustomer(customerId).getCustomerName(), "");

        assertNull(shop.getCustomer(customerId).getCustomerCard());
    }

    @Test
    public void testModifyInvalidNameCustomerRecord() {
        loginAs(User.RoleEnum.ShopManager);

        assertThrows(InvalidCustomerNameException.class, () -> shop.modifyCustomer(customerId, null, null));

        assertThrows(InvalidCustomerNameException.class, () -> shop.modifyCustomer(customerId, "", null));
    }

    @Test
    public void testModifyPointsOnCustomerCard() throws InvalidCustomerIdException, UnauthorizedException, InvalidCustomerCardException {
        loginAs(User.RoleEnum.ShopManager);

        assertEquals(customerCard, shop.getCustomer(customerId).getCustomerCard());
        shop.modifyPointsOnCard(customerCard, 20);
        assertEquals(20, shop.getCustomer(customerId).getPoints(), 0);
        shop.modifyPointsOnCard(customerCard, 35);
        assertEquals(55, shop.getCustomer(customerId).getPoints(), 0);
    }

    @AfterClass
    public static void testDeleteCustomerRecord() throws InvalidCustomerIdException, UnauthorizedException {
        loginAs(User.RoleEnum.ShopManager);

        assertTrue(shop.deleteCustomer(customerId));
        assertTrue(shop.deleteCustomer(customerId2));
        assertTrue(shop.deleteCustomer(newCustomerId));
    }
}
