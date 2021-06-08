package it.polito.ezshop.integrationTests;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderRFIDIntegrationTest extends BaseIntegrationTest {

    private static Integer productId;
    private static Integer orderId;


    @BeforeClass
    public static void createProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidProductIdException, InvalidQuantityException {
        loginAs(User.RoleEnum.ShopManager);

        //create a productType
        productId = shop.createProductType("test", "000000000000", 3.2, "");
        assertTrue(productId>0);
        boolean isPositionUpdated = shop.updatePosition(productId, "1-A-1");
        assertTrue(isPositionUpdated);
        //update balance
        boolean isBalanceUpdated = shop.recordBalanceUpdate(+10);
        assertTrue(isBalanceUpdated);
        //issue order
        orderId = shop.issueOrder("000000000000", 10, 1);
        assertTrue(orderId > 0);
        //pay order
        orderId = shop.payOrderFor("000000000000", 10, 1);
        assertTrue(orderId > 0);
    }

    @Test
    public void testValidRecordOrderArrivalRFID() throws UnauthorizedException, InvalidLocationException, InvalidOrderIdException, InvalidRFIDException {
        loginAs(User.RoleEnum.ShopManager);
        boolean isRecorded = shop.recordOrderArrivalRFID(orderId, "0000000010");
        assertTrue(isRecorded);
    }


    @AfterClass
    public static void deleteProduct() throws UnauthorizedException, InvalidProductIdException {
        loginAs(User.RoleEnum.ShopManager);
        //delete productType created initially
        boolean isProductDeleted = shop.deleteProductType(productId);
        assertTrue(isProductDeleted);
    }

}
