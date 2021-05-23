package it.polito.ezshop.integrationTests;

import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrderIntegrationTest extends BaseIntegrationTest {

    private static Integer productId;

    @BeforeClass
    public static void createProduct_UpdateBalance() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidProductIdException {
        loginAs(User.RoleEnum.ShopManager);

        //create a productType
        productId = shop.createProductType("Nutella", "000000000000", 3.2, "");
        shop.updatePosition(productId, "1-A-1");

        //update balance
        shop.recordBalanceUpdate(+10);
    }

    //Scenario 3-1
    @Test
    public void testValidIssueOrder() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException {
        loginAs(User.RoleEnum.ShopManager);

        Integer orderId = shop.issueOrder("000000000000", 10, 1);

        assertTrue(orderId > 0);
    }

    @Test
    public void testInvalidCodeIssueOrder(){
        loginAs(User.RoleEnum.ShopManager);

        assertThrows(InvalidProductCodeException.class, () -> shop.issueOrder("", 10, 1));
    }

    @Test
    public void testInvalidQuantityIssueOrder(){
        loginAs(User.RoleEnum.ShopManager);

        assertThrows(InvalidQuantityException.class, () -> shop.issueOrder("000000000000", -1, 1));
    }

    @Test
    public void testInvalidPriceIssueOrder(){
        loginAs(User.RoleEnum.ShopManager);

        assertThrows(InvalidPricePerUnitException.class, () -> shop.issueOrder("000000000000", 10, -1));
    }

    @Test
    public void testUnauthorizedIssueOrder() {
        shop.logout();

        assertThrows(UnauthorizedException.class,() -> shop.issueOrder("000000000000", 10, 1));
    }

    //Scenario 3.4
    @Test
    public void testValidPayOrderFor() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException {
        loginAs(User.RoleEnum.ShopManager);

        Integer orderId = shop.payOrderFor("000000000000", 10, 1);

        assertTrue(orderId > 0);
    }

    @Test
    public void testInvalidQuantityPayOrderFor() {
        loginAs(User.RoleEnum.ShopManager);

        assertThrows(InvalidQuantityException.class, () -> shop.payOrderFor("000000000000", -1, 1));
    }

    @Test
    public void testInvalidCodePayOrderFor() {
        loginAs(User.RoleEnum.ShopManager);

        assertThrows(InvalidProductCodeException.class, () -> shop.payOrderFor("0", 10, 1));
    }

    @Test
    public void testInvalidPricePayOrderFor() {
        loginAs(User.RoleEnum.ShopManager);

        assertThrows(InvalidPricePerUnitException.class, () -> shop.payOrderFor("000000000000", 10, -1));
    }

    @Test
    public void testUnauthorizedPayOrderFor() {
        shop.logout();

        assertThrows(UnauthorizedException.class,() -> {
            shop.payOrderFor("000000000000", 10, 1);;
        });
    }

    //Scenario 3-2
    @Test
    public void testValidPayOrder() throws UnauthorizedException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductCodeException {
        loginAs(User.RoleEnum.ShopManager);

        Integer orderId = shop.payOrderFor("000000000000", 10, 1);

        assertTrue(orderId > 0);
    }

    //Scenario 3-3
    @Test
    public void testValidRecordOrderArrival() throws UnauthorizedException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidOrderIdException {
        loginAs(User.RoleEnum.ShopManager);
        Integer orderId = shop.payOrderFor("000000000000", 10, 1);
        assertTrue(shop.recordOrderArrival(orderId));
    }


    @AfterClass
    public static void DeleteProduct() throws UnauthorizedException, InvalidProductIdException {
        loginAs(User.RoleEnum.ShopManager);
        //delete productType created initially
        shop.deleteProductType(productId);

    }
}
