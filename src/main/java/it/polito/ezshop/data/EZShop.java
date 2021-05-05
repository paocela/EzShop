package it.polito.ezshop.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import it.polito.ezshop.exceptions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.polito.ezshop.model.User;
import it.polito.ezshop.model.ProductType;

public class EZShop implements EZShopInterface {

    private final static String DATABASE_URL = "jdbc:sqlite:data/db.sqlite";

    ConnectionSource connectionSource;
    Dao<User, Integer> userDao;
    Dao<ProductType, Integer> productTypeDao;

    private User userLogged;

    private User getUserLogged() {
        return userLogged;
    }

    private void setUserLogged(User userLogged) {
        this.userLogged = userLogged;
    }

    public EZShop() {
        try {
            connectionSource = new JdbcConnectionSource(DATABASE_URL);

            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, ProductType.class);

            userDao = DaoManager.createDao(connectionSource, User.class);
            productTypeDao = DaoManager.createDao(connectionSource, ProductType.class);

        } catch (SQLException e) {
            // TODO DEFINE LOGGING STRATEGY
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
    }

    /**
     * This method creates a new user with given username, password and role. The returned value is a unique identifier
     * for the new user.
     *
     * @param username the username of the new user. This value should be unique and not empty.
     * @param password the password of the new user. This value should not be empty.
     * @param role     the role of the new user. This value should not be empty and it should assume
     *                 one of the following values : "Administrator", "Cashier", "ShopManager"
     * @return The id of the new user ( > 0 ).
     * -1 if there is an error while saving the user or if another user with the same username exists
     * @throws InvalidUsernameException If the username has an invalid value (empty or null)
     * @throws InvalidPasswordException If the password has an invalid value (empty or null)
     * @throws InvalidRoleException     If the role has an invalid value (empty, null or not among the set of admissible values)
     */
    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {

        User.RoleEnum roleEnum;

        // Verify role validity
        try {
            roleEnum = User.RoleEnum.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException();
        }

        // Verify password validity
        if (password == null || password.isEmpty()) {
            throw new InvalidUsernameException();
        }

        // Verify username validity
        if (username == null || username.isEmpty()) {
            throw new InvalidPasswordException();
        }

        Integer returnId = -1;

        // Verify username is free
        try {
            QueryBuilder<User, Integer> usernameFreeQueryBuilder = userDao.queryBuilder();

            usernameFreeQueryBuilder.setCountOf(true).where().eq("username", username);

            boolean isUsernameAvailable = usernameFreeQueryBuilder.countOf() == 0;

            if (isUsernameAvailable) {
                // Create user
                User user = new User(username, password, roleEnum);
                userDao.create(user);

                returnId = user.getId();
            }

        } catch (SQLException e) {
            // TODO DEFINE LOGGING STRATEGY
            e.printStackTrace();
        }

        return returnId;
    }

    /**
     * This method deletes the user with given id. It can be invoked only after a user with role "Administrator" is
     * logged in.
     *
     * @param id the user id, this value should not be less than or equal to 0 or null.
     * @return true if the user was deleted
     * false if the user cannot be deleted
     * @throws InvalidUserIdException if id is less than or equal to 0 or if it is null.
     * @throws UnauthorizedException  if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {

        authorize(User.RoleEnum.Administrator);

        // Verify id validity
        if (id == null || id <= 0) {
            throw new InvalidUserIdException();
        }

        boolean isDeleted = false;

        try {
            if (userDao.idExists(id)) {
                isDeleted = userDao.deleteById(id) == 1;
            }
        } catch (SQLException e) {
            // TODO DEFINE LOGGING STRATEGY
            e.printStackTrace();
        }

        return isDeleted;
    }

    /**
     * This method returns the list of all registered users. It can be invoked only after a user with role "Administrator" is
     * logged in.
     *
     * @return a list of all registered users. If there are no users the list should be empty
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public List<User> getAllUsers() throws UnauthorizedException {

        authorize(User.RoleEnum.Administrator);

        List<User> allUsers = new ArrayList<>();
        try {
            allUsers = userDao.queryForAll();
        } catch (SQLException e) {
            // TODO DEFINE LOGGING STRATEGY
            e.printStackTrace();
        }

        return allUsers;
    }

    /**
     * This method returns a User object with given id. It can be invoked only after a user with role "Administrator" is
     * logged in.
     *
     * @param id the id of the user
     * @return the requested user if it exists, null otherwise
     * @throws InvalidUserIdException if id is less than or equal to zero or if it is null
     * @throws UnauthorizedException  if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public it.polito.ezshop.data.User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {

        authorize(User.RoleEnum.Administrator);

        // Verify id validity
        if (id == null || id <= 0) {
            throw new InvalidUserIdException();
        }

        User returnUser = null;

        try {
            returnUser = userDao.queryForId(id);
        } catch (SQLException e) {
            // TODO DEFINE LOGGING STRATEGY
            e.printStackTrace();
        }

        return returnUser;
    }

    /**
     * This method updates the role of a user with given id. It can be invoked only after a user with role "Administrator" is
     * logged in.
     *
     * @param id   the id of the user
     * @param role the new role the user should be assigned to
     * @return true if the update was successful, false if the user does not exist
     * @throws InvalidUserIdException if the user Id is less than or equal to 0 or if it is null
     * @throws InvalidRoleException   if the new role is empty, null or not among one of the following : {"Administrator", "Cashier", "ShopManager"}
     * @throws UnauthorizedException  if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {

        authorize(User.RoleEnum.Administrator);

        // Verify id validity
        if (id == null || id <= 0) {
            throw new InvalidUserIdException();
        }

        User.RoleEnum roleEnum;

        // Verify role validity
        try {
            roleEnum = User.RoleEnum.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException();
        }

        boolean isUpdated = false;

        try {
            UpdateBuilder<User, Integer> updateUserQueryBuilder = userDao.updateBuilder();
            updateUserQueryBuilder.updateColumnValue("role", roleEnum.toString())
                    .where().eq("id", id);

            updateUserQueryBuilder.update();
            isUpdated = true;

            // TODO REFRESH THE LOGGED USER IF HE IS THE UPDATED ONE

        } catch (SQLException e) {
            // TODO DEFINE LOGGING STRATEGY
            e.printStackTrace();
        }

        return isUpdated;
    }

    /**
     * This method lets a user with given username and password login into the system
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return an object of class User filled with the logged user's data if login is successful, null otherwise ( wrong credentials or db problems)
     * @throws InvalidUsernameException if the username is empty or null
     * @throws InvalidPasswordException if the password is empty or null
     */
    @Override
    public it.polito.ezshop.data.User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {

        User returnUser = null;

        // Verify username validity
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException();
        }

        // Verify password validity
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException();
        }

        try {
            QueryBuilder<User, Integer> loginQueryBuilder = userDao.queryBuilder();
            loginQueryBuilder.where().eq("username", username).and().eq("password", hashPassword(password));

            returnUser = loginQueryBuilder.queryForFirst();
            setUserLogged(returnUser);

        } catch (SQLException e) {
            // TODO DEFINE LOGGING STRATEGY
            e.printStackTrace();
        }

        return returnUser;
    }

    /**
     * This method makes a user to logout from the system
     *
     * @return true if the logout is successful, false otherwise (there is no logged user)
     */
    @Override
    public boolean logout() {
        User loggedUser = getUserLogged();

        if (loggedUser != null) {
            setUserLogged(null);
            return true;
        }
        return false;
    }

    /**
     * This method creates a product type and returns its unique identifier. It can be invoked only after a user with role "Administrator"
     * or "ShopManager" is logged in.
     *
     * @param description  the description of product to be created
     * @param productCode  the unique barcode of the product
     * @param pricePerUnit the price per single unit of product
     * @param note         the notes on the product (if null an empty string should be saved as description)
     * @return The unique identifier of the new product type ( > 0 ).
     * -1 if there is an error while saving the product type or if it exists a product with the same barcode
     * @throws InvalidProductDescriptionException if the product description is null or empty
     * @throws InvalidProductCodeException        if the product code is null or empty, if it is not a number or if it is not a valid barcode
     * @throws InvalidPricePerUnitException       if the price per unit si less than or equal to 0
     * @throws UnauthorizedException              if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        Integer returnId = -1;

        if (note == null) {
            note = "";
        }

        QueryBuilder<ProductType, Integer> usernameFreeQueryBuilder = productTypeDao.queryBuilder().setCountOf(true);

        try {
            usernameFreeQueryBuilder.where().eq("code", productCode);
            boolean isProductCodeAvailable = productTypeDao.countOf(usernameFreeQueryBuilder.prepare()) == 0;

            if (isProductCodeAvailable) {
                ProductType productType = new ProductType(description, productCode, pricePerUnit, note);
                productTypeDao.create(productType);

                returnId = productType.getId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO LOG
        }


        return returnId;
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        return false;
    }

    @Override
    public List<it.polito.ezshop.data.ProductType> getAllProductTypes() throws UnauthorizedException {
        return new ArrayList<>();
    }

    @Override
    public it.polito.ezshop.data.ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        return null;
    }

    @Override
    public List<it.polito.ezshop.data.ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        return null;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        return false;
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        return false;
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        return null;
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        return false;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        return null;
    }

    @Override
    public String createCard() throws UnauthorizedException {
        return null;
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        return false;
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        return null;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return false;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        return false;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        return null;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        return 0;
    }

    private void authorize(User.RoleEnum... roles) throws UnauthorizedException {
        if (userLogged == null || !Arrays.asList(roles).contains(User.RoleEnum.valueOf(userLogged.getRole()))) {
            throw new UnauthorizedException();
        }
    }

    private String hashPassword(String password) {
        // TODO DEFINE HASHING ALGORITHM
        return password;
    }
}
