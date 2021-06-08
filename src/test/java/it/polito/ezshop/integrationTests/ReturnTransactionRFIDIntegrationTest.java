package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ReturnTransactionRFIDIntegrationTest extends BaseIntegrationTest {

    private static Integer productId = null;
    private static Integer transactionId = null;
    private static Integer returnTransactionId = null;

    private static final String productCode = "2345678901234";
    private static final double productPricePerUnit = 2;
    private static final Integer transactionProductAmount = 1;
    private static final String creditCard = "4485370086510891";

    private static final String productRFIDFrom = "1123456789";
    private static final int productQuantity = 8;
    private String currentRFID = null;

    private static final ArrayList<String> productRFIDs = new ArrayList<String>(Arrays.asList("1123456789", "1123456790", "1123456791",
            "1123456792", "1123456793", "1123456794", "1123456795", "1123456796"));
    @BeforeClass
    public static void createProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidProductIdException, InvalidQuantityException, InvalidRFIDException, InvalidOrderIdException {
        loginAs(User.RoleEnum.ShopManager);

        productId = shop.createProductType("Any", productCode, productPricePerUnit, null);
        assertTrue(productId >= 0);

        boolean isPositionUpdate = shop.updatePosition(productId, "1-A-1");
        assertTrue(isPositionUpdate);

        Integer orderId = shop.issueOrder(productCode, productQuantity, 1.2);
        assertNotNull(orderId);

        boolean isBalanceUpdated = shop.recordBalanceUpdate(50000);
        assertTrue(isBalanceUpdated);

        boolean isPaid = shop.payOrder(orderId);
        assertTrue(isPaid);

        boolean isArrived = shop.recordOrderArrivalRFID(orderId, productRFIDFrom);
        assertTrue(isArrived);
    }

    @Before
    public void createTransactionWithProduct() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidRFIDException {
        loginAs(User.RoleEnum.Cashier);

        transactionId = shop.startSaleTransaction();
        assertTrue(transactionId >= 0);
        currentRFID = productRFIDs.remove(0);

        boolean isProductAdded = shop.addProductToSaleRFID(transactionId, currentRFID);
        assertTrue(isProductAdded);

        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);
    }

    @Test
    public void testCompletedReturnTransactionCreditCard() throws InvalidTransactionIdException, UnauthorizedException, InvalidCreditCardException, InvalidRFIDException {

        loginAs(User.RoleEnum.Cashier);
        boolean isPayed = shop.receiveCreditCardPayment(transactionId, creditCard);
        assertTrue(isPayed);

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        loginAs(User.RoleEnum.Cashier);

        returnTransactionId = shop.startReturnTransaction(transactionId);
        assertTrue(transactionId >= 0);

        boolean isProductReturned = shop.returnProductRFID(returnTransactionId, currentRFID);
        assertTrue(isProductReturned);

        boolean isClosed= shop.endReturnTransaction(returnTransactionId, true);
        assertTrue(isClosed);

        double change= shop.returnCreditCardPayment(returnTransactionId, creditCard);
        assertEquals(change, productPricePerUnit * 1, .01);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(1 * productPricePerUnit, balanceBefore-balanceAfter, .01);
    }

    @Test
    public void testCompletedReturnTransactionCash() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidRFIDException {
        loginAs(User.RoleEnum.Cashier);

        double total = 7;
        double change = shop.receiveCashPayment(transactionId, total);
        assertEquals(change, total - (productPricePerUnit * transactionProductAmount), .01);

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        loginAs(User.RoleEnum.Cashier);
        returnTransactionId = shop.startReturnTransaction(transactionId);
        assertTrue(transactionId >= 0);

        boolean isProductReturned = shop.returnProductRFID(returnTransactionId, currentRFID);
        assertTrue(isProductReturned);

        boolean isClosed= shop.endReturnTransaction(returnTransactionId, true);
        assertTrue(isClosed);

        double returnChange = shop.returnCashPayment(returnTransactionId);
        assertEquals(returnChange, productPricePerUnit * 1, .01);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(1 * productPricePerUnit, balanceBefore-balanceAfter, .01);
    }

    @AfterClass
    public static void deleteProduct() throws UnauthorizedException, InvalidProductIdException {
        loginAs(User.RoleEnum.ShopManager);
        boolean isDeleted = shop.deleteProductType(productId);

        assertTrue(isDeleted);
    }
}
