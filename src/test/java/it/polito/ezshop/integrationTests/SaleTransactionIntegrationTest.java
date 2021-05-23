package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.*;

import static org.junit.Assert.*;

public class SaleTransactionIntegrationTest extends BaseIntegrationTest {

    private static Integer productId = null;
    private static Integer transactionId = null;
    private static final String productCode = "0123456789012";
    private static final double productPricePerUnit = 2;
    private static final Integer transactionProductAmount = 3;
    private static final String creditCard = "5100293991053009";

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
    public void createTransactionWithProduct() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException {
        loginAs(User.RoleEnum.Cashier);

        transactionId = shop.startSaleTransaction();
        assertTrue(transactionId >= 0);

        boolean isProductAdded = shop.addProductToSale(transactionId, productCode, transactionProductAmount);
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

        assertEquals(transactionProductAmount * productPricePerUnit, balanceAfter - balanceBefore, .01);
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

        assertEquals(transactionProductAmount * productPricePerUnit * (1 - discount), balanceAfter - balanceBefore, .01);
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

        assertEquals(transactionProductAmount * productPricePerUnit * (1 - discount), balanceAfter - balanceBefore, .01);
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

        assertEquals(transactionProductAmount * productPricePerUnit, balanceAfter - balanceBefore, .01);
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
    public void testCompletedCash() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidQuantityException, InvalidProductCodeException {

        loginAs(User.RoleEnum.Administrator);
        double balanceBefore = shop.computeBalance();

        int removedAmount = 1;

        loginAs(User.RoleEnum.Cashier);
        boolean isRemoved = shop.deleteProductFromSale(transactionId, productCode, removedAmount);
        assertTrue(isRemoved);

        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        double change = shop.receiveCashPayment(transactionId, 12);
        assertTrue(change >= 0);

        loginAs(User.RoleEnum.Administrator);
        double balanceAfter = shop.computeBalance();

        assertEquals((transactionProductAmount - removedAmount) * productPricePerUnit, balanceAfter - balanceBefore, .01);
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

    @Test //UC6.1 + 7.3
    public void testInsufficientFunds() throws InvalidTransactionIdException, UnauthorizedException, InvalidCreditCardException {

        loginAs(User.RoleEnum.Cashier);
        boolean isClosed = shop.endSaleTransaction(transactionId);
        assertTrue(isClosed);

        boolean isPayed = shop.receiveCreditCardPayment(transactionId, creditCard);
        assertFalse(isPayed);

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
