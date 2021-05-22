package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.InvalidCustomerNameException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.User;
import org.junit.Test;
import static org.junit.Assert.*;

public class CustomerIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testCreateValidCustomer() throws InvalidCustomerNameException, UnauthorizedException {
        loginAs(User.RoleEnum.ShopManager);

        Integer customerId = shop.defineCustomer("validCustomerName");

        assertTrue(customerId > 0);
    }

    @Test
    public void testCreateInvalidCustomer() {
        loginAs(User.RoleEnum.ShopManager);

        assertThrows(InvalidCustomerNameException.class,() -> {
            shop.defineCustomer(null);
        });
    }

    @Test
    public void testCreateCustomerUnauthorized() {
        shop.logout();

        assertThrows(UnauthorizedException.class,() -> {
            shop.defineCustomer("validCustomerName");
        });
    }

}
