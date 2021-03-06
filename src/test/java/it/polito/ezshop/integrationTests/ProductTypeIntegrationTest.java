package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProductTypeIntegrationTest extends BaseIntegrationTest {

    private static Integer productId = null;

    //Scenario 1.1
    @Before
    public void testCreateProductType() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        loginAs(User.RoleEnum.ShopManager);

        productId = shop.createProductType("test", "0123456789012", 1.0, "anything");
        assertTrue(productId > 0);
    }


/*    //scenario 1.1
    @Test
    public void testCreateProductType() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        loginAs(User.RoleEnum.ShopManager);

        productId2 = shop.createProductType("Nutella", "000000000000", 3.2, "something");
        assertTrue(productId2 > 0);
    }*/

    //scenario 1.2
    @Test
    public void testUpdateLocation() throws UnauthorizedException, InvalidLocationException, InvalidProductIdException, InvalidProductCodeException {
        loginAs(User.RoleEnum.ShopManager);

        boolean isUpdated;

        productId = shop.getProductTypeByBarCode("0123456789012").getId();

        isUpdated = shop.updatePosition(productId, "1-B-1");

        assertTrue(isUpdated);
    }

    //scenario 1.3
    @Test
    public void testUpdateProductType() throws UnauthorizedException, InvalidProductIdException, InvalidProductCodeException, InvalidProductDescriptionException, InvalidPricePerUnitException {
        loginAs(User.RoleEnum.ShopManager);

        boolean isUpdated;

        productId = shop.getProductTypeByBarCode("0123456789012").getId();

        isUpdated = shop.updateProduct(productId, "newTest", "00000000000000", 3.5, "something else");

        assertTrue(isUpdated);
    }

    @After
    public void deleteProduct() throws UnauthorizedException, InvalidProductIdException {
        loginAs(User.RoleEnum.ShopManager);
        shop.deleteProductType(productId);
    }


/*    //scenario 1.3
    @AfterClass
    public static void testUpdateProductType() throws UnauthorizedException, InvalidProductIdException, InvalidProductCodeException, InvalidProductDescriptionException, InvalidPricePerUnitException {
        loginAs(User.RoleEnum.ShopManager);

        boolean isUpdated;

        productId = shop.getProductTypeByBarCode("0123456789012").getId();

        isUpdated = shop.updateProduct(productId, "Nutella2", "00000000000000", 3.5, "something else");

        assertTrue(isUpdated);
    }*/

}
