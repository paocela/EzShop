package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReturnTransactionIntegrationTest extends BaseIntegrationTest {

    private static Integer productId = null;
    private static Integer transactionId = null;
    private static Integer returnTransactionId = null;

    private static final String productCode = "2345678901234";
    private static final double productPricePerUnit = 2;
    private static final Integer transactionProductAmount = 3;
    private static final Integer returnTransactionProductAmount = 2;
    private static final String creditCard = "4485370086510891";

    @BeforeClass
    public static void createProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidProductIdException {
        loginAs(User.RoleEnum.ShopManager);

        productId = shop.createProductType("Any", productCode, productPricePerUnit, null);
        assertTrue(productId >= 0);

        boolean isPositionUpdate = shop.updatePosition(productId, "1-A-1");
        assertTrue(isPositionUpdate);

        boolean isQuantityUpdate = shop.updateQuantity(productId, 100);
        assertTrue(isQuantityUpdate);
    }

    @Before
    public void createEndedSaleTransaction() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException {
        loginAs(User.RoleEnum.Cashier);

        transactionId = shop.startSaleTransaction();
        assertTrue(transactionId >= 0);

        boolean isProductAdded = shop.addProductToSale(transactionId, productCode, transactionProductAmount);
        assertTrue(isProductAdded);

        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);
    }

    @Test
    public void testCompletedReturnTransactionCreditCard() throws InvalidTransactionIdException, UnauthorizedException, InvalidCreditCardException, InvalidQuantityException, InvalidProductCodeException {

        loginAs(User.RoleEnum.Cashier);
        boolean isPayed = shop.receiveCreditCardPayment(transactionId, creditCard);
        assertTrue(isPayed);

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        loginAs(User.RoleEnum.Cashier);

        returnTransactionId = shop.startReturnTransaction(transactionId);
        assertTrue(transactionId >= 0);

        boolean isProductReturned = shop.returnProduct(returnTransactionId, productCode, returnTransactionProductAmount);
        assertTrue(isProductReturned);

        boolean isClosed= shop.endReturnTransaction(returnTransactionId, true);
        assertTrue(isClosed);

        double change= shop.returnCreditCardPayment(returnTransactionId, creditCard);
        assertEquals(change, productPricePerUnit * returnTransactionProductAmount, .01);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(returnTransactionProductAmount * productPricePerUnit, balanceBefore-balanceAfter, .01);
    }

    @Test
    public void testCompletedReturnTransactionCash() throws InvalidTransactionIdException, UnauthorizedException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException {
        loginAs(User.RoleEnum.Cashier);

        double total = 7;
        double change = shop.receiveCashPayment(transactionId, total);
        assertEquals(change, total - (productPricePerUnit * transactionProductAmount), .01);

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        loginAs(User.RoleEnum.Cashier);
        returnTransactionId = shop.startReturnTransaction(transactionId);
        assertTrue(transactionId >= 0);

        boolean isProductReturned = shop.returnProduct(returnTransactionId, productCode, returnTransactionProductAmount);
        assertTrue(isProductReturned);

        boolean isClosed= shop.endReturnTransaction(returnTransactionId, true);
        assertTrue(isClosed);

        double returnChange = shop.returnCashPayment(returnTransactionId);
        assertEquals(returnChange, productPricePerUnit * returnTransactionProductAmount, .01);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(returnTransactionProductAmount * productPricePerUnit, balanceBefore-balanceAfter, .01);
    }

    @AfterClass
    public static void deleteProduct() throws UnauthorizedException, InvalidProductIdException {
        loginAs(User.RoleEnum.ShopManager);
        boolean isDeleted = shop.deleteProductType(productId);

        assertTrue(isDeleted);
    }
}
