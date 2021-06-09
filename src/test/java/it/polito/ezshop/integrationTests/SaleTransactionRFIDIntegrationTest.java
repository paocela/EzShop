package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SaleTransactionRFIDIntegrationTest extends BaseIntegrationTest {

    private static Integer productId = null;
    private static Integer transactionId = null;
    private static final String productCode = "0123456789012";
    private static final String productRFIDFrom = "012123456789";
    private static final int productQuantity = 8;
    private static final double productPricePerUnit = 2;
    private static final String creditCard = "5100293991053009";
    private String currentRFID = null;

    private static final ArrayList<String> productRFIDs = new ArrayList<String>(Arrays.asList("012123456789", "012123456790", "012123456791",
            "012123456792", "012123456793", "012123456794", "012123456795", "012123456796"));
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

    }

    @Test       //UC6.1 + 7.1
    public void testCompletedCreditCard() throws InvalidTransactionIdException, UnauthorizedException, InvalidCreditCardException {

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        loginAs(User.RoleEnum.Cashier);
        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        boolean isPayed = shop.receiveCreditCardPayment(transactionId, creditCard);
        assertTrue(isPayed);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(1 * productPricePerUnit, balanceAfter - balanceBefore, .01);
    }

    @Test       //UC6.2
    public void testProductDiscount() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidDiscountRateException, InvalidProductCodeException {
        double discount = .2;

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        loginAs(User.RoleEnum.Cashier);
        boolean isDiscounted = shop.applyDiscountRateToProduct(transactionId, productCode, discount);
        assertTrue(isDiscounted);

        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        double change = shop.receiveCashPayment(transactionId, 12);
        assertTrue(change >= 0);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(1 * productPricePerUnit * (1 - discount), balanceAfter - balanceBefore, .01);
    }

    @Test       //UC6.3
    public void testSaleDiscount() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidDiscountRateException {

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        double discount = 0.2;

        loginAs(User.RoleEnum.Cashier);
        boolean isDiscounted = shop.applyDiscountRateToSale(transactionId, discount);
        assertTrue(isDiscounted);

        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        double change = shop.receiveCashPayment(transactionId, 12);
        assertTrue(change >= 0);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(1 * productPricePerUnit * (1 - discount), balanceAfter - balanceBefore, .01);
    }

    @Test       //UC6.4
    public void testLoyaltyCard() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidCustomerNameException, InvalidCustomerIdException, InvalidCustomerCardException {

        loginAs(User.RoleEnum.Administrator);

        Integer customerId = shop.defineCustomer("testLoyaltyCardCustomer");
        assertTrue(customerId > 0);

        String customerCard = shop.createCard();
        assertTrue(customerCard != null && !customerCard.isEmpty());

        boolean isAttached = shop.attachCardToCustomer(customerCard, customerId);
        assertTrue(isAttached);

        double balanceBefore = shop.computeBalance();

        loginAs(User.RoleEnum.Cashier);

        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        int points = shop.computePointsForSale(transactionId);
        assertTrue(points >= 0);

        boolean isModified = shop.modifyPointsOnCard(customerCard, points);
        assertTrue(isModified);

        double change = shop.receiveCashPayment(transactionId, 12);
        assertTrue(change >= 0);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(1 * productPricePerUnit, balanceAfter - balanceBefore, .01);
    }

    @Test       //UC6.5
    public void testCancelled() throws InvalidTransactionIdException, UnauthorizedException {
        loginAs(User.RoleEnum.Cashier);

        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        boolean isCancelled = shop.deleteSaleTransaction(transactionId);
        assertTrue(isCancelled);
    }

    @Test       //UC6.6 + UC 7.4
    public void testCompletedCash() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidQuantityException, InvalidRFIDException {

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        loginAs(User.RoleEnum.Cashier);
        boolean isRemoved = shop.deleteProductFromSaleRFID(transactionId, currentRFID);
        assertTrue(isRemoved);

        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        double change = shop.receiveCashPayment(transactionId, 12);
        assertTrue(change >= 0);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals(0 * productPricePerUnit, balanceAfter - balanceBefore, .01);
    }

    @Test       //UC6.1 + 7.2
    public void testFailedCreditCard() throws InvalidTransactionIdException, UnauthorizedException {

        loginAs(User.RoleEnum.Cashier);
        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        assertThrows(InvalidCreditCardException.class, () -> {
            shop.receiveCreditCardPayment(transactionId, "45465488484");
        });

    }


    @After
    public void deleteTransaction() throws InvalidTransactionIdException, UnauthorizedException {
        loginAs(User.RoleEnum.Cashier);
        shop.deleteSaleTransaction(transactionId);
    }

    @AfterClass
    public static void deleteProduct() throws UnauthorizedException, InvalidProductIdException {
        loginAs(User.RoleEnum.ShopManager);
        boolean isDeleted = shop.deleteProductType(productId);

        assertTrue(isDeleted);
    }
}
