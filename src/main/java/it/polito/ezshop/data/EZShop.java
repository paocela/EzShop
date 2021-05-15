package it.polito.ezshop.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import it.polito.ezshop.exceptions.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.polito.ezshop.model.*;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.ReturnTransaction;
import it.polito.ezshop.model.SaleTransaction;
import it.polito.ezshop.model.User;
import it.polito.ezshop.model.Order;
import it.polito.ezshop.model.BalanceOperation;

public class EZShop implements EZShopInterface {

    private final static String DATABASE_URL = "jdbc:sqlite:data/db.sqlite";
    private final static String CREDIT_CARDS_FILE_PATH = "src/main/java/it/polito/ezshop/utils/CreditCards.txt";

    ConnectionSource connectionSource;
    Dao<User, Integer> userDao;
    Dao<ProductType, Integer> productTypeDao;
    Dao<Customer, Integer> customerDao;
    Dao<SaleTransaction, Integer> saleTransactionDao;
    Dao<ReturnTransaction, Integer> returnTransactionDao;
    Dao<Order, Integer> orderDao;
    Dao<BalanceOperation, Integer> balanceOperationDao;
    Dao<CreditCard, String> creditCardDao;

    private User userLogged = null;
    private SaleTransaction ongoingTransaction = null;
    private ReturnTransaction ongoingReturnTransaction = null;

    public EZShop() {
        Logger.setGlobalLogLevel(Log.Level.ERROR);

        try {
            connectionSource = new JdbcConnectionSource(DATABASE_URL);

            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, ProductType.class);
            TableUtils.createTableIfNotExists(connectionSource, Customer.class);
            TableUtils.createTableIfNotExists(connectionSource, SaleTransaction.class);
            TableUtils.createTableIfNotExists(connectionSource, SaleTransactionRecord.class);
            TableUtils.createTableIfNotExists(connectionSource, ReturnTransaction.class);
            TableUtils.createTableIfNotExists(connectionSource, ReturnTransactionRecord.class);

            TableUtils.createTableIfNotExists(connectionSource, Order.class);
            TableUtils.createTableIfNotExists(connectionSource, BalanceOperation.class);
            TableUtils.createTableIfNotExists(connectionSource, CreditCard.class);

            userDao = DaoManager.createDao(connectionSource, User.class);
            productTypeDao = DaoManager.createDao(connectionSource, ProductType.class);
            customerDao = DaoManager.createDao(connectionSource, Customer.class);
            saleTransactionDao = DaoManager.createDao(connectionSource, SaleTransaction.class);
            returnTransactionDao = DaoManager.createDao(connectionSource, ReturnTransaction.class);
            orderDao = DaoManager.createDao(connectionSource, Order.class);
            balanceOperationDao = DaoManager.createDao(connectionSource, BalanceOperation.class);
            creditCardDao = DaoManager.createDao(connectionSource, CreditCard.class);

            loadCreditCardsFromUtils();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method should reset the application to its base state: balance zero, no transacations, no products
     */
    @Override
    public void reset() {
        // need to remove entries from db tables sale_transactions, sale_transaction_records, return_transaction,
        // return_transaction_records, products, balance_operations
        try {
            // TODO REMOVE COMMENT FROM RETURN TRANSACTIONS LINES
            DeleteBuilder<SaleTransaction, Integer> saleTransactionDeleteBuilder = saleTransactionDao.deleteBuilder();
            saleTransactionDeleteBuilder.delete();
            DeleteBuilder<SaleTransactionRecord, Integer> saleTransactionRecordDeleteBuilder = saleTransactionRecordDao.deleteBuilder();
            saleTransactionRecordDeleteBuilder.delete();
            //DeleteBuilder<ReturnTransaction, Integer> returnTransactionDeleteBuilder = returnTransactionDao.deleteBuilder();
            //returnTransactionDeleteBuilder.delete();
            //DeleteBuilder<ReturnTransactionRecord, Integer> ReturnTransactionRecordDeleteBuilder = saleTransactionDao.deleteBuilder();
            //deleteBuilder.delete();
            DeleteBuilder<ProductType, Integer> ProductTypeDeleteBuilder = productTypeDao.deleteBuilder();
            ProductTypeDeleteBuilder.delete();
            DeleteBuilder<BalanceOperation, Integer> BalanceOperationDeleteBuilder = balanceOperationDao.deleteBuilder();
            BalanceOperationDeleteBuilder.delete();

            creditCardDao.deleteBuilder().delete();
            loadCreditCardsFromUtils();


        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
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
                User user = new User(username, hashPassword(password), roleEnum);
                userDao.create(user);

                returnId = user.getId();
            }

        } catch (SQLException e) {
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
    public List<it.polito.ezshop.data.User> getAllUsers() throws UnauthorizedException {

        authorize(User.RoleEnum.Administrator);

        List<it.polito.ezshop.data.User> returnUsers = new ArrayList<>();
        try {

            List<User> allUsers = userDao.queryForAll();
            returnUsers.addAll(allUsers);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnUsers;
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

        } catch (SQLException e) {
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
            userLogged = returnUser;

        } catch (SQLException e) {
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
        if (userLogged != null) {
            userLogged = null;
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

        User.RoleEnum[] roles = {User.RoleEnum.Administrator, User.RoleEnum.ShopManager};
        authorize(roles);

        //(if null an empty string should be saved as description
        if (note == null) {
            note = "";
        }

        //return -1 if there is an error while saving the product type or if it exists a product with the same barcode
        Integer returnId = -1;

        // Verify description validity
        if (description == null || description.isEmpty()) {
            throw new InvalidProductDescriptionException();
        }

        // Verify code validity
        if (productCode == null || productCode.isEmpty() || !validateBarcode(productCode)) {
            throw new InvalidProductCodeException();
        }

        // Verify pricePerUnit validity
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }

        // Create ProductType
        QueryBuilder<ProductType, Integer> productFreeQueryBuilder = productTypeDao.queryBuilder().setCountOf(true);

        try {
            productFreeQueryBuilder.where().eq("code", productCode);
            boolean isProductCodeAvailable = productTypeDao.countOf(productFreeQueryBuilder.prepare()) == 0;

            if (isProductCodeAvailable) {
                ProductType productType = new ProductType(description, productCode, pricePerUnit, note);
                productTypeDao.create(productType);

                returnId = productType.getId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnId;
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {

        User.RoleEnum[] roles = {User.RoleEnum.Administrator, User.RoleEnum.ShopManager};
        authorize(roles);

        boolean isUpdated = false;


        // Verify id validity
        if (id == null || id <= 0) {
            throw new InvalidProductIdException();
        }

        // Verify description validity
        if (newDescription == null || newDescription.isEmpty()) {
            throw new InvalidProductDescriptionException();
        }

        // Verify code validity
        if (newCode == null || newCode.isEmpty() || !validateBarcode(newCode)) {
            throw new InvalidProductCodeException();
        }

        // Verify pricePerUnit validity
        if (newPrice <= 0) {
            throw new InvalidPricePerUnitException();
        }


        // Update ProductType
        QueryBuilder<ProductType, Integer> productFreeQueryBuilder = productTypeDao.queryBuilder().setCountOf(true);

        try {

            productFreeQueryBuilder.where().eq("id", id);
            boolean isProductIdavailable = productTypeDao.countOf(productFreeQueryBuilder.prepare()) == 0;
            if (!isProductIdavailable) {

                productFreeQueryBuilder.where().eq("code", newCode);
                boolean isProductCodeAvailable = productTypeDao.countOf(productFreeQueryBuilder.prepare()) == 0;
                if (!isProductCodeAvailable) {

                    UpdateBuilder<ProductType, Integer> updateProductQueryBuilder = productTypeDao.updateBuilder();
                    updateProductQueryBuilder.updateColumnValue("code", newCode)
                            .updateColumnValue("description", newDescription)
                            .updateColumnValue("pricePerUnit", newPrice)
                            .updateColumnValue("notes", newNote)
                            .where().eq("id", id);

                    updateProductQueryBuilder.update();
                    isUpdated = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUpdated;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {

        User.RoleEnum[] roles = {User.RoleEnum.Administrator, User.RoleEnum.ShopManager};
        authorize(roles);

        // Verify id validity
        if (id == null || id <= 0) {
            throw new InvalidProductIdException();
        }

        boolean isDeleted = false;

        // delete ProductType
        QueryBuilder<ProductType, Integer> productFreeQueryBuilder = productTypeDao.queryBuilder().setCountOf(true);
        try {
            productFreeQueryBuilder.where().eq("id", id);
            boolean isProductCodeAvailable = productTypeDao.countOf(productFreeQueryBuilder.prepare()) == 0;

            if (!isProductCodeAvailable) {
                productTypeDao.deleteById(id);
                isDeleted = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isDeleted;
    }

    @Override
    public List<it.polito.ezshop.data.ProductType> getAllProductTypes() throws UnauthorizedException {

        User.RoleEnum[] roles = {User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier};
        authorize(roles);

        List<it.polito.ezshop.data.ProductType> products = null;
        try {
            products = new ArrayList<>(productTypeDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {

        User.RoleEnum[] roles = {User.RoleEnum.Administrator, User.RoleEnum.ShopManager};
        authorize(roles);

        ProductType product = null;

        // Verify code validity
        if (barCode == null || barCode.isEmpty() || !validateBarcode(barCode)) {
            throw new InvalidProductCodeException();
        }

        QueryBuilder<ProductType, Integer> productFreeQueryBuilder = productTypeDao.queryBuilder().setCountOf(true);

        try {
            productFreeQueryBuilder.where().eq("code", barCode);
            boolean isProductCodeAvailable = productTypeDao.countOf(productFreeQueryBuilder.prepare()) == 0;

            if (!isProductCodeAvailable) {
                List<ProductType> products = productTypeDao.queryForEq("code", barCode);
                if (products.size() == 1) {
                    product = products.get(0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public List<it.polito.ezshop.data.ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {

        User.RoleEnum[] roles = {User.RoleEnum.Administrator, User.RoleEnum.ShopManager};
        authorize(roles);

        List<it.polito.ezshop.data.ProductType> products = null;

        // Verify description validity
        if (description == null) {
            description = "";
        }

        QueryBuilder<ProductType, Integer> productFreeQueryBuilder = productTypeDao.queryBuilder().setCountOf(true);

        try {
            productFreeQueryBuilder.where().eq("description", description);
            boolean isProductCodeAvailable = productTypeDao.countOf(productFreeQueryBuilder.prepare()) == 0;

            if (!isProductCodeAvailable) {
                products = new ArrayList<>(productTypeDao.queryForEq("description", description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * This method updates the quantity of product available in store. <toBeAdded> can be negative but the final updated
     * quantity cannot be negative. The product should have a location assigned to it.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productId the id of the product to be updated
     * @param toBeAdded the quantity to be added. If negative it decrease the available quantity of <toBeAdded> elements.
     * @return true if the update was successful
     * false if the product does not exists, if <toBeAdded> is negative and the resulting amount would be
     * negative too or if the product type has not an assigned location.
     * @throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
     * @throws UnauthorizedException     if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {

        User.RoleEnum[] roles = {User.RoleEnum.Administrator, User.RoleEnum.ShopManager};
        authorize(roles);

        boolean isUpdated = false;

        // Verify id validity
        if (productId == null || productId <= 0) {
            throw new InvalidProductIdException();
        }


        // Update quantity
        QueryBuilder<ProductType, Integer> productFreeQueryBuilder = productTypeDao.queryBuilder().setCountOf(true);

        try {

            productFreeQueryBuilder.where().eq("id", productId);
            boolean isProductIdvailable = productTypeDao.countOf(productFreeQueryBuilder.prepare()) == 0;
            if (!isProductIdvailable) {

                it.polito.ezshop.data.ProductType product = productTypeDao.queryForId(productId);
                int new_quantity = product.getQuantity() + toBeAdded;


                //if <toBeAdded> is negative and the resulting amount would be
                //     *          negative too or if the product type has not an assigned location
                if (new_quantity >= 0 && !product.getLocation().isEmpty()) {

                    UpdateBuilder<ProductType, Integer> updateProductQueryBuilder = productTypeDao.updateBuilder();
                    updateProductQueryBuilder.updateColumnValue("quantity", new_quantity)
                            .where().eq("id", productId);

                    updateProductQueryBuilder.update();
                    isUpdated = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUpdated;
    }

    /**
     * This method assign a new position to the product with given product id. The position has the following format :
     * <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>
     * The position should be unique (unless it is an empty string, in this case this means that the product type
     * has not an assigned location). If <newPos> is null or empty it should reset the position of given product type.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productId the id of the product to be updated
     * @param newPos    the new position the product should be placed to.
     * @return true if the update was successful
     * false if the product does not exists or if <newPos> is already assigned to another product
     * @throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
     * @throws InvalidLocationException  if the product location is in an invalid format (not <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>)
     * @throws UnauthorizedException     if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        User.RoleEnum[] roles = {User.RoleEnum.Administrator, User.RoleEnum.ShopManager};
        authorize(roles);

        boolean isUpdated = false;

        // Verify id validity
        if (productId == null || productId <= 0) {
            throw new InvalidProductIdException();
        }

        if (newPos == null || newPos.isEmpty()) {
            newPos = "";
        } else {
            String[] pos = newPos.split("-");

            if (pos.length < 3) {
                throw new InvalidLocationException();
            }

            try {
                Integer.parseInt(pos[0]);
                Integer.parseInt(pos[2]);
            } catch (NumberFormatException nfe) {
                throw new InvalidLocationException();
            }
        }


        // Update location
        QueryBuilder<ProductType, Integer> productFreeQueryBuilder = productTypeDao.queryBuilder().setCountOf(true);

        boolean isProductLocationvailable = true;

        try {

            //If <newPos> is null or empty it should reset the position of given product type.
            if (!newPos.isEmpty()) {
                productFreeQueryBuilder.where().eq("position", newPos);
                isProductLocationvailable = productTypeDao.countOf(productFreeQueryBuilder.prepare()) == 0;
            }

            if (isProductLocationvailable) {

                UpdateBuilder<ProductType, Integer> updateProductQueryBuilder = productTypeDao.updateBuilder();
                updateProductQueryBuilder.updateColumnValue("position", newPos)
                        .where().eq("id", productId);

                updateProductQueryBuilder.update();
                isUpdated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUpdated;
    }

    /**
     * This method issues an order of <quantity> units of product with given <productCode>, each unit will be payed
     * <pricePerUnit> to the supplier. <pricePerUnit> can differ from the re-selling price of the same product. The
     * product might have no location assigned in this step.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productCode  the code of the product that we should order as soon as possible
     * @param quantity     the quantity of product that we should order
     * @param pricePerUnit the price to correspond to the supplier (!= than the resale price of the shop) per unit of
     *                     product
     * @return the id of the order (> 0)
     * -1 if the product does not exists, if there are problems with the db
     * @throws InvalidProductCodeException  if the productCode is not a valid bar code, if it is null or if it is empty
     * @throws InvalidQuantityException     if the quantity is less than or equal to 0
     * @throws InvalidPricePerUnitException if the price per unit of product is less than or equal to 0
     * @throws UnauthorizedException        if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager);

        //return -1 if the product does not exists, if there are problems with the db
        Integer orderId = -1;

        // Verify code validity
        if (productCode == null || productCode.isEmpty() || !validateBarcode(productCode)) {
            throw new InvalidProductCodeException();
        }
        //Verify quantity validity
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }

        // Verify pricePerUnit validity
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }

        //Create order in db
        try {
            Order order = new Order(productCode, quantity, pricePerUnit);
            orderDao.create(order);
            orderId = order.getOrderId();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;

    }

    /**
     * This method directly orders and pays <quantity> units of product with given <productCode>, each unit will be payed
     * <pricePerUnit> to the supplier. <pricePerUnit> can differ from the re-selling price of the same product. The
     * product might have no location assigned in this step.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productCode  the code of the product to be ordered
     * @param quantity     the quantity of product to be ordered
     * @param pricePerUnit the price to correspond to the supplier (!= than the resale price of the shop) per unit of
     *                     product
     * @return the id of the order (> 0)
     * -1 if the product does not exists, if the balance is not enough to satisfy the order, if there are some
     * problems with the db
     * @throws InvalidProductCodeException  if the productCode is not a valid bar code, if it is null or if it is empty
     * @throws InvalidQuantityException     if the quantity is less than or equal to 0
     * @throws InvalidPricePerUnitException if the price per unit of product is less than or equal to 0
     * @throws UnauthorizedException        if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager);

        //return -1 if the product does not exists, if there are problems with the db
        Integer orderId = -1;

        // Verify code validity
        if (productCode == null || productCode.isEmpty() || !validateBarcode(productCode)) {
            throw new InvalidProductCodeException();
        }

        //Verify quantity validity
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }

        // Verify pricePerUnit validity
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }

        double currentBalance = computeBalance();
        double orderCost = pricePerUnit * quantity;

        //Create order in db
        try {
            if (Double.doubleToRawLongBits(currentBalance - orderCost) >= 0) {
                Order order = new Order(productCode, quantity, pricePerUnit);
                order.setStatus("PAYED");
                orderDao.create(order);
                //update balance
                recordBalanceUpdate(-orderCost);
                //set the orderId to return
                orderId = order.getOrderId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }

    /**
     * This method change the status the order with given <orderId> into the "PAYED" state. The order should be either
     * issued (in this case the status changes) or payed (in this case the method has no effect).
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param orderId the id of the order to be ORDERED
     * @return true if the order has been successfully ordered
     * false if the order does not exist or if it was not in an ISSUED/ORDERED state
     * @throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
     * @throws UnauthorizedException   if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager);

        //if the order does not exist or if it was not in an ISSUED/ORDERED state
        boolean isPayed = false;

        //check orderId validity
        if (orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException();
        }
        //search for the order to pay
        Order orderToUpdate = (Order) getAllOrders().stream().filter(
                order -> orderId.equals(order.getOrderId())).findAny()
                .orElse(null);

        try {
            if (orderToUpdate != null && orderToUpdate.getStatus().equals("ISSUED")) {

                double currentBalance = computeBalance();
                double orderCost = orderToUpdate.getPricePerUnit() * orderToUpdate.getQuantity();

                if (Double.doubleToRawLongBits(currentBalance - orderCost) >= 0) {
                    Order.StatusEnum status = Order.StatusEnum.PAYED;
                    //update order state
                    UpdateBuilder<Order, Integer> updateOrderQueryBuilder = orderDao.updateBuilder();
                    updateOrderQueryBuilder.updateColumnValue("status", status)
                            .where().eq("orderId", orderId);
                    updateOrderQueryBuilder.update();
                    //update balance
                    recordBalanceUpdate(-orderCost);

                    isPayed = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isPayed;
    }

    /**
     * This method records the arrival of an order with given <orderId>. This method changes the quantity of available product.
     * The product type affected must have a location registered. The order should be either in the PAYED state (in this
     * case the state will change to the COMPLETED one and the quantity of product type will be updated) or in the
     * COMPLETED one (in this case this method will have no effect at all).
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param orderId the id of the order that has arrived
     * @return true if the operation was successful
     * false if the order does not exist or if it was not in an ORDERED/COMPLETED state
     * @throws InvalidOrderIdException  if the order id is less than or equal to 0 or if it is null.
     * @throws InvalidLocationException if the ordered product type has not an assigned location.
     * @throws UnauthorizedException    if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager);

        // TODO SHOULD CHECK PRODUCT LOCATION

        //return false if the order does not exist or if it was not in an ORDERED/COMPLETED state
        boolean isRecorded = false;

        //check orderId validity
        if (orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException();
        }

        Order orderToUpdate = (Order) getAllOrders().stream().filter(
                order -> orderId.equals(order.getOrderId())).
                findAny()
                .orElse(null);


        try {
            if (orderToUpdate != null && orderToUpdate.getStatus().equals("PAYED")) {

                ProductType productToUpdate = getProductTypeByBarCode(orderToUpdate.getProductCode());

                if (productToUpdate != null && !productToUpdate.getLocation().isEmpty()) {

                    updateQuantity(productToUpdate.getId(), orderToUpdate.getQuantity());

                    Order.StatusEnum status = Order.StatusEnum.COMPLETED;

                    UpdateBuilder<Order, Integer> updateOrderQueryBuilder = orderDao.updateBuilder();
                    updateOrderQueryBuilder.updateColumnValue("status", status)
                            .where().eq("orderId", orderId);
                    updateOrderQueryBuilder.update();

                    isRecorded = true;
                }
            }

        } catch (SQLException | InvalidProductCodeException | InvalidProductIdException e) {
            e.printStackTrace();
        }

        return isRecorded;
    }

    @Override
    public List<it.polito.ezshop.data.Order> getAllOrders() throws UnauthorizedException {
        List<it.polito.ezshop.data.Order> orderList = new ArrayList<>();

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager);

        // get order list
        try {
            orderList.addAll(orderDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;

    }

    /**
     * This method saves a new customer into the system. The customer's name should be unique.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerName the name of the customer to be registered
     * @return the id (>0) of the new customer if successful, -1 otherwise
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws UnauthorizedException        if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        Integer customerId = -1;

        // verify customer name validity
        if (customerName == null || customerName.isEmpty()) {
            throw new InvalidCustomerNameException();
        }

        // verify if logged in user
        if (userLogged == null) {
            throw new UnauthorizedException();
        }

        // create customer in db
        try {
            Customer customer = new Customer(customerName);
            customerDao.create(customer);
            customerId = customer.getId();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerId;
    }

    /**
     * This method updates the data of a customer with given <id>. This method can be used to assign/delete a card to a
     * customer. If <newCustomerCard> has a numeric value than this value will be assigned as new card code, if it is an
     * empty string then any existing card code connected to the customer will be removed and, finally, it it assumes the
     * null value then the card code related to the customer should not be affected from the update. The card code should
     * be unique and should be a string of 10 digits.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id              the id of the customer to be updated
     * @param newCustomerName the new name to be assigned
     * @param newCustomerCard the new card code to be assigned. If it is empty it means that the card must be deleted,
     *                        if it is null then we don't want to update the cardNumber
     * @return true if the update is successful
     * false if the update fails ( cardCode assigned to another user, db unreacheable)
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws InvalidCustomerCardException if the customer card is empty, null or if it is not in a valid format (string with 10 digits)
     * @throws UnauthorizedException        if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        boolean isUpdated = false;
        // TODO how is the card number generated in the GUI?
        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        // verify name validity
        if (newCustomerName == null || newCustomerName.isEmpty()) {
            throw new InvalidCustomerNameException();
        }

        // verify id validity
        if (id == null || id < 1) {
            throw new InvalidCustomerIdException();
        }

        // verify card validity
        if (newCustomerCard != null && !Arrays.asList(10, 0).contains(newCustomerCard.length())) {
            throw new InvalidCustomerCardException();
        }

        if (newCustomerCard == null) {
            // update only name
            try {
                UpdateBuilder<Customer, Integer> updateCustomerQueryBuilder = customerDao.updateBuilder();
                updateCustomerQueryBuilder.updateColumnValue("name", newCustomerName)
                        .where().eq("id", id);
                updateCustomerQueryBuilder.update();
                isUpdated = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (newCustomerCard.length() != 0) {
            // update both
            try {
                UpdateBuilder<Customer, Integer> updateCustomerQueryBuilder = customerDao.updateBuilder();
                updateCustomerQueryBuilder.updateColumnValue("name", newCustomerName)
                        .updateColumnValue("card", newCustomerCard)
                        .where().eq("id", id);
                updateCustomerQueryBuilder.update();
                isUpdated = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // update name and remove card
            try {
                UpdateBuilder<Customer, Integer> updateCustomerQueryBuilder = customerDao.updateBuilder();
                updateCustomerQueryBuilder.updateColumnValue("name", newCustomerName)
                        .updateColumnValue("card", "")
                        .where().eq("id", id);
                updateCustomerQueryBuilder.update();
                isUpdated = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isUpdated;
    }

    /**
     * This method deletes a customer with given id from the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer to be deleted
     * @return true if the customer was successfully deleted
     * false if the user does not exists or if we have problems to reach the db
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws UnauthorizedException      if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        boolean isDeleted = false;

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        // Verify id validity
        if (id == null || id <= 0) {
            throw new InvalidCustomerIdException();
        }

        // delete customer
        try {
            if (customerDao.idExists(id)) {
                isDeleted = customerDao.deleteById(id) == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    /**
     * This method returns a customer with given id.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer
     * @return the customer with given id
     * null if that user does not exists
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws UnauthorizedException      if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        Customer returnCustomer = null;

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        // verify id validity
        if (id == null || id < 1) {
            throw new InvalidCustomerIdException();
        }

        // get customer by id
        try {
            returnCustomer = customerDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnCustomer;
    }

    /**
     * This method returns a list containing all registered users.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the list of all the customers registered
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public List<it.polito.ezshop.data.Customer> getAllCustomers() throws UnauthorizedException {
        List<it.polito.ezshop.data.Customer> customerList = new ArrayList<>();

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        // get customer list
        try {
            customerList.addAll(customerDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }

    /**
     * This method returns a string containing the code of a new assignable card.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the code of a new available card. An empty string if the db is unreachable
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public String createCard() throws UnauthorizedException {
        String cardCode = null;
        boolean found = false;

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        while (!found) {
            // create card code
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            cardCode = Long.toString(number);

            // check if card is already attached to any customer
            QueryBuilder<Customer, Integer> cardFreeQueryBuilder = customerDao.queryBuilder().setCountOf(true);
            try {
                // check if card is attached to any customer
                cardFreeQueryBuilder.where().eq("card", cardCode);
                found = customerDao.countOf(cardFreeQueryBuilder.prepare()) == 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return cardCode;
    }

    /**
     * This method assigns a card with given card code to a customer with given identifier. A card with given card code
     * can be assigned to one customer only.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerCard the number of the card to be attached to a customer
     * @param customerId   the id of the customer the card should be assigned to
     * @return true if the operation was successful
     * false if the card is already assigned to another user, if there is no customer with given id, if the db is unreachable
     * @throws InvalidCustomerIdException   if the id is null, less than or equal to 0.
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     * @throws UnauthorizedException        if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        boolean isAttached = false;

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        // Verify id validity
        if (customerId == null || customerId <= 0) {
            throw new InvalidCustomerIdException();
        }

        // verify customer card validity
        if (customerCard == null || customerCard.length() != 10) {
            throw new InvalidCustomerCardException();
        }

        // attach card to customer
        try {
            UpdateBuilder<Customer, Integer> updateCustomerQueryBuilder = customerDao.updateBuilder();
            updateCustomerQueryBuilder.updateColumnValue("card", customerCard)
                    .where().eq("id", customerId);

            updateCustomerQueryBuilder.update();
            isAttached = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAttached;
    }

    /**
     * This method updates the points on a card adding to the number of points available on the card the value assumed by
     * <pointsToBeAdded>. The points on a card should always be greater than or equal to 0.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerCard    the card the points should be added to
     * @param pointsToBeAdded the points to be added or subtracted ( this could assume a negative value)
     * @return true if the operation is successful
     * false   if there is no card with given code,
     * if pointsToBeAdded is negative and there were not enough points on that card before this operation,
     * if we cannot reach the db.
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     * @throws UnauthorizedException        if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        Customer customer;
        boolean isUpdated = false;
        boolean isCardAvailable;
        Integer initPoints;
        Integer finalPoints;

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        // verify customer card validity
        if (customerCard == null || customerCard.length() != 10) {
            throw new InvalidCustomerCardException();
        }

        // prepare query builder for customer-card existence
        QueryBuilder<Customer, Integer> customerFreeQueryBuilder = customerDao.queryBuilder().setCountOf(true);

        try {
            // check if card is attached to any customer
            customerFreeQueryBuilder.where().eq("card", customerCard);
            isCardAvailable = customerDao.countOf(customerFreeQueryBuilder.prepare()) == 1;

            if (isCardAvailable) {
                // query for customer card
                customer = customerDao.queryForEq("card", customerCard).get(0);
                initPoints = customer.getPoints();

                // update points
                finalPoints = initPoints + pointsToBeAdded;

                // check points value and save if correct
                if (finalPoints >= 0) {
                    UpdateBuilder<Customer, Integer> updateCustomerQueryBuilder = customerDao.updateBuilder();
                    updateCustomerQueryBuilder.updateColumnValue("points", finalPoints)
                            .where().eq("id", customer.getId());

                    updateCustomerQueryBuilder.update();
                    isUpdated = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    /**
     * This method starts a new sale transaction and returns its unique identifier.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the id of the transaction (greater than or equal to 0)
     */
    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        System.out.println("[DEV] startSaleTransaction()");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        Integer returnId = -1;

        SaleTransaction newSaleTransaction = new SaleTransaction();

        // Transaction must be persisted into the database to have a valid id
        try {
            saleTransactionDao.create(newSaleTransaction);
            saleTransactionDao.assignEmptyForeignCollection(newSaleTransaction, "records");
            returnId = newSaleTransaction.getId();

            ongoingTransaction = newSaleTransaction;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnId;
    }

    /**
     * This method adds a product to a sale transaction decreasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode   the barcode of the product to be added
     * @param amount        the quantity of product to be added
     * @return true if the operation is successful
     * false   if the product code does not exist,
     * if the quantity of product cannot satisfy the request,
     * if the transaction id does not identify a started and open transaction.
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException   if the product code is empty, null or invalid
     * @throws InvalidQuantityException      if the quantity is less than 0
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        System.out.println("[DEV] addProductToSale(" + transactionId + "," + productCode + "," + amount + ")");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        // Verify id validity
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        if (productCode == null || productCode.isEmpty() || !validateBarcode(productCode)) {
            throw new InvalidProductCodeException();
        }

        // Verify amount validity (I included zero as it makes no sense excluding it)
        if (amount <= 0) {
            throw new InvalidQuantityException();
        }

        SaleTransaction transaction = getOngoingTransactionById(transactionId);

        if (transaction == null || transaction.getStatus() != SaleTransaction.StatusEnum.STARTED) {
            // No valid transaction found
            return false;
        }

        boolean isAdded = false;
        try {

            isAdded = TransactionManager.callInTransaction(connectionSource,
                    () -> {
                        QueryBuilder<ProductType, Integer> getProductQueryBuilder = productTypeDao.queryBuilder();
                        ProductType product = getProductQueryBuilder.where().eq("code", productCode).queryForFirst();

                        if (product == null) {
                            // No valid product found
                            return false;
                        }

                        ForeignCollection<SaleTransactionRecord> transactionRecords = transaction.getRecords();

                        Optional<SaleTransactionRecord> optionalRecord = transactionRecords.stream().filter(
                                record -> record.getBarCode().equals(productCode)
                        ).findFirst();

                        if (optionalRecord.isPresent()) {
                            SaleTransactionRecord existingRecord = optionalRecord.get();

                            // There is an existing record for input product, updating it
                            int amountAlreadyInTransaction = existingRecord.getAmount();

                            if (product.getQuantity() < amountAlreadyInTransaction + amount) {
                                // Not enough products to fulfill
                                return false;
                            }

                            // There is an existing record for input product, increasing quantity
                            existingRecord.setAmount(amountAlreadyInTransaction + amount);
                            existingRecord.refreshTotalPrice();

                            // Update record
                            transactionRecords.update(existingRecord);

                        } else {
                            // No existing record for input product, creating a new one

                            if (product.getQuantity() < amount) {
                                // Not enough products to fulfill
                                return false;
                            }

                            SaleTransactionRecord newRecord = new SaleTransactionRecord(transaction, product, amount);

                            // Add new record
                            transactionRecords.add(newRecord);
                        }


                        transaction.refreshAmount();

                        saleTransactionDao.update(transaction);
                        ongoingTransaction = transaction;

                        return true;
                    });

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAdded;
    }

    /**
     * This method deletes a product from a sale transaction increasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode   the barcode of the product to be deleted
     * @param amount        the quantity of product to be deleted
     * @return true if the operation is successful
     * false   if the product code does not exist,
     * if the quantity of product cannot satisfy the request,
     * if the transaction id does not identify a started and open transaction.
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException   if the product code is empty, null or invalid
     * @throws InvalidQuantityException      if the quantity is less than 0
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        System.out.println("[DEV] deleteProductFromSale(" + transactionId + "," + productCode + "," + amount + ")");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        // Verify id validity
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        if (productCode == null || productCode.isEmpty() || !validateBarcode(productCode)) {
            throw new InvalidProductCodeException();
        }

        // Verify amount validity (I included zero as it makes no sense excluding it)
        if (amount <= 0) {
            throw new InvalidQuantityException();
        }

        SaleTransaction transaction = getOngoingTransactionById(transactionId);

        if (transaction == null || transaction.getStatus() != SaleTransaction.StatusEnum.STARTED) {
            // No valid transaction found
            return false;
        }

        boolean isRemoved = false;
        try {

            isRemoved = TransactionManager.callInTransaction(connectionSource,
                    () -> {
                        QueryBuilder<ProductType, Integer> getProductQueryBuilder = productTypeDao.queryBuilder();
                        ProductType product = getProductQueryBuilder.where().eq("code", productCode).queryForFirst();

                        if (product == null) {
                            // No valid product found
                            return false;
                        }

                        ForeignCollection<SaleTransactionRecord> transactionRecords = transaction.getRecords();

                        Optional<SaleTransactionRecord> optionalRecord = transactionRecords.stream().filter(
                                record -> record.getBarCode().equals(productCode)
                        ).findFirst();

                        if (!optionalRecord.isPresent()) {
                            // No transaction record for this product
                            return false;
                        }

                        SaleTransactionRecord existingRecord = optionalRecord.get();

                        int amountAlreadyInTransaction = existingRecord.getAmount();

                        if (amount > amountAlreadyInTransaction) {
                            // The quantity cannot satisfy the request
                            return false;
                        } else if (amount == amountAlreadyInTransaction) {

                            // Remove record
                            transactionRecords.remove(existingRecord);
                        } else {
                            existingRecord.setAmount(amountAlreadyInTransaction - amount);
                            existingRecord.refreshTotalPrice();

                            // Update record
                            transactionRecords.update(existingRecord);
                        }

                        transaction.setRecords(transactionRecords);
                        transaction.refreshAmount();

                        saleTransactionDao.update(transaction);
                        ongoingTransaction = transaction;

                        return true;
                    });

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isRemoved;
    }

    /**
     * This method applies a discount rate to all units of a product type with given type in a sale transaction. The
     * discount rate should be greater than or equal to 0 and less than 1.
     * The sale transaction should be started and open.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode   the barcode of the product to be discounted
     * @param discountRate  the discount rate of the product
     * @return true if the operation is successful
     * false   if the product code does not exist,
     * if the transaction id does not identify a started and open transaction.
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException   if the product code is empty, null or invalid
     * @throws InvalidDiscountRateException  if the discount rate is less than 0 or if it greater than or equal to 1.00
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        System.out.println("[DEV] applyDiscountRateToProduct(" + transactionId + "," + productCode + "," + discountRate + ")");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        // Verify id validity
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        if (productCode == null || productCode.isEmpty() || !validateBarcode(productCode)) {
            throw new InvalidProductCodeException();
        }

        // Verify discountRate validity
        if (discountRate < 0 || discountRate >= 1) {
            throw new InvalidDiscountRateException();
        }

        SaleTransaction transaction = getOngoingTransactionById(transactionId);

        if (transaction == null || transaction.getStatus() != SaleTransaction.StatusEnum.STARTED) {
            // No valid transaction found
            return false;
        }

        boolean isDiscounted = false;
        try {

            isDiscounted = TransactionManager.callInTransaction(connectionSource,
                    () -> {
                        ForeignCollection<SaleTransactionRecord> transactionRecords = transaction.getRecords();

                        Optional<SaleTransactionRecord> optionalRecord = transactionRecords.stream().filter(
                                record -> record.getBarCode().equals(productCode)
                        ).findFirst();

                        if (!optionalRecord.isPresent()) {
                            // No transaction record for this product
                            return false;
                        }

                        SaleTransactionRecord existingRecord = optionalRecord.get();

                        existingRecord.setDiscountRate(discountRate);
                        existingRecord.refreshTotalPrice();

                        transactionRecords.update(existingRecord);

                        transaction.setRecords(transactionRecords);
                        transaction.refreshAmount();

                        saleTransactionDao.update(transaction);
                        ongoingTransaction = transaction;

                        return true;
                    });

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isDiscounted;
    }

    /**
     * This method applies a discount rate to the whole sale transaction.
     * The discount rate should be greater than or equal to 0 and less than 1.
     * The sale transaction can be either started or closed but not already payed.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param discountRate  the discount rate of the sale
     * @return true if the operation is successful
     * false if the transaction does not exists
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidDiscountRateException  if the discount rate is less than 0 or if it greater than or equal to 1.00
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {

        System.out.println("[DEV] applyDiscountRateToSale(" + transactionId + "," + discountRate + ")");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        // Verify transaction id validity
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        // Verify discount rate validity
        if (discountRate < 0 || discountRate >= 1) {
            throw new InvalidDiscountRateException();
        }

        boolean isDiscountApplied = false;

        SaleTransaction transaction = getOngoingTransactionById(transactionId);

        if (transaction != null) {
            transaction.setDiscountRate(discountRate);
            transaction.refreshAmount();

            try {
                saleTransactionDao.update(transaction);
                isDiscountApplied = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return isDiscountApplied;
    }

    /**
     * This method returns the number of points granted by a specific sale transaction.
     * Every 10€ the number of points is increased by 1 (i.e. 19.99€ returns 1 point, 20.00€ returns 2 points).
     * If the transaction with given id does not exist then the number of points returned should be -1.
     * The transaction may be in any state (open, closed, payed).
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @return the points of the sale (1 point for each 10€) or -1 if the transaction does not exists
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {

        System.out.println("[DEV] computePointsForSale(" + transactionId + ")");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        int returnPoints = -1;

        try {
            SaleTransaction transaction = saleTransactionDao.queryForId(transactionId);

            if (transaction != null) {
                returnPoints = (int) Math.floor(transaction.getAmount() / 10);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnPoints;
    }

    /**
     * This method closes an opened transaction. After this operation the
     * transaction is persisted in the system's memory.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @return true    if the transaction was successfully closed
     * false   if the transaction does not exist,
     * if it has already been closed,
     * if there was a problem in registering the data
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {

        System.out.println("[DEV] endSaleTransaction(" + transactionId + ")");
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        // Verify id validity
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        boolean transactionStatusChanged = false;

        SaleTransaction ongoingTransaction = getOngoingTransactionById(transactionId);

        if (ongoingTransaction != null && ongoingTransaction.getStatus() == SaleTransaction.StatusEnum.STARTED) {
            // Close the transaction
            ongoingTransaction.setStatus(SaleTransaction.StatusEnum.CLOSED);

            try {
                saleTransactionDao.update(ongoingTransaction);
                transactionStatusChanged = true;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return transactionStatusChanged;
    }

    /**
     * This method deletes a sale transaction with given unique identifier from the system's data store.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the transaction to be deleted
     * @return true if the transaction has been successfully deleted,
     * false   if the transaction doesn't exist,
     * if it has been payed,
     * if there are some problems with the db
     * @throws InvalidTransactionIdException if the transaction id number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean deleteSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        System.out.println("[DEV] deleteSaleTransaction(" + transactionId + ")");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        // Verify id validity
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        boolean isDeleted = false;

        try {
            DeleteBuilder<SaleTransaction, Integer> deleteTransactionQueryBuilder = saleTransactionDao.deleteBuilder();

            deleteTransactionQueryBuilder.where().eq("id", transactionId)
                    .and().not().eq("status", SaleTransaction.StatusEnum.PAID);

            isDeleted = deleteTransactionQueryBuilder.delete() == 1;

            // Unset the ongoing transaction if it is the deleted one
            if (isDeleted && ongoingTransaction.getId().equals(transactionId)) {
                ongoingTransaction = null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isDeleted;
    }

    /**
     * This method returns  a closed sale transaction.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the CLOSED Sale transaction
     * @return the transaction if it is available (transaction closed), null otherwise
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        System.out.println("[DEV] getSaleTransaction(" + transactionId + ")");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        // Verify id validity
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        SaleTransaction returnTransaction = null;

        try {
            QueryBuilder<SaleTransaction, Integer> closedTransactionByIdQueryBuilder = saleTransactionDao.queryBuilder();

            closedTransactionByIdQueryBuilder.where().eq("id", transactionId)
                    .and().eq("status", SaleTransaction.StatusEnum.CLOSED);

            returnTransaction = closedTransactionByIdQueryBuilder.queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnTransaction;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {

        System.out.println("[DEV] startReturnTransaction()");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        Integer returnId = -1;

        if (saleNumber == null || saleNumber <= 0) throw new InvalidTransactionIdException();

        SaleTransaction transaction = getSaleTransaction(saleNumber);

        if (transaction != null) {
            ReturnTransaction newReturnTransaction = new ReturnTransaction(saleNumber);

            try {
                returnTransactionDao.create(newReturnTransaction);
                returnTransactionDao.assignEmptyForeignCollection(newReturnTransaction, "records");
                returnId = newReturnTransaction.getReturnId();

            } catch (SQLException e) {
                ongoingReturnTransaction = newReturnTransaction;
                e.printStackTrace();
            }
        }

        return returnId;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        boolean productadded = false;

        if (returnId == null || returnId <= 0) throw new InvalidTransactionIdException();
        if (productCode == null || productCode.isEmpty() || !validateBarcode(productCode))
            throw new InvalidProductCodeException();
        if (amount <= 0) throw new InvalidQuantityException();


        ProductType product = getProductTypeByBarCode(productCode);
        if (product == null) return false;

        ReturnTransaction returnTransaction = getReturnTransaction(returnId);
        if (returnTransaction == null) return false;

        SaleTransaction transaction = getSaleTransaction(returnTransaction.getTicketNumber());
        if (transaction == null) return false;

        for (TicketEntry salerecord : transaction.getEntries()) {

            if (salerecord.getBarCode().equals(productCode)) {

                int saleAmount = salerecord.getAmount();
                if (amount > saleAmount) return false;

                try {
                    ReturnTransactionRecord returnRecord = new ReturnTransactionRecord(product, amount, product.getPricePerUnit()*amount);
                    productadded= returnTransaction.addReturnTransactionRecord(returnRecord);

                    if (productadded) {
                        returnTransactionDao.update(returnTransaction);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();

                }
                return productadded;
            }
        }

        return false;
    }

    private ReturnTransaction getReturnTransaction(Integer returnId) {

        ReturnTransaction returnTransaction = null;

        try {
            returnTransaction = returnTransactionDao.queryForId(returnId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnTransaction;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        if (returnId == null || returnId<=0) throw new InvalidTransactionIdException();

        boolean transactionStatusChanged = false;

        ReturnTransaction ongoingReturnTransaction = this.ongoingReturnTransaction;

        if (ongoingReturnTransaction == null || !ongoingReturnTransaction.getReturnId().equals(returnId)) {
            // TODO CHECK IF THIS CAN ACTUALLY HAPPEN VIA THE GUI
        }

        if (ongoingReturnTransaction != null && ongoingReturnTransaction.getStatus() == ReturnTransaction.StatusEnum.STARTED) {
            // Close the transaction
            ongoingReturnTransaction.setStatus(ReturnTransaction.StatusEnum.CLOSED);
            transactionStatusChanged = true;

            if (commit){
                try {
                    SaleTransaction transaction = getSaleTransaction(ongoingReturnTransaction.getTicketNumber());
                    if (transaction != null){
                        ongoingReturnTransaction.getRecords().forEach((p) ->{
                            transaction.updateSaleTransactionRecord(p.getProductType(), -(p.getQuantity()));
                        });
                        saleTransactionDao.update(transaction);
                        returnTransactionDao.update(ongoingReturnTransaction);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return transactionStatusChanged;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        System.out.println("[DEV] deleteReturnTransaction(" + returnId + ")");

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        if (returnId == null || returnId<=0) throw new InvalidTransactionIdException();

        boolean isDeleted = false;

        try {

            ReturnTransaction returnTransaction = returnTransactionDao.queryForId(returnId);

            if (returnTransaction!=null && returnTransaction.getStatus()==ReturnTransaction.StatusEnum.CLOSED){
                SaleTransaction transaction = getSaleTransaction(ongoingReturnTransaction.getTicketNumber());
                if (transaction != null){
                    ongoingReturnTransaction.getRecords().forEach((p) ->{
                        transaction.updateSaleTransactionRecord(p.getProductType(), p.getQuantity());
                    });
                    saleTransactionDao.update(transaction);
                    if (returnTransactionDao.deleteById(returnId)==1) isDeleted=true;
                }
            }

            // Unset the ongoing returntransaction if it is the deleted one
            if (isDeleted && ongoingReturnTransaction.getReturnId().equals(returnId)) {
                ongoingReturnTransaction = null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return isDeleted;

    }

    /**
     * This method record the payment of a sale transaction with cash and returns the change (if present).
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the transaction that the customer wants to pay
     * @param cash          the cash received by the cashier
     * @return the change (cash - sale price)
     * -1   if the sale does not exists,
     * if the cash is not enough,
     * if there is some problemi with the db
     * @throws InvalidTransactionIdException if the  number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     * @throws InvalidPaymentException       if the cash is less than or equal to 0
     */
    @Override
    public double receiveCashPayment(Integer transactionId, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {

        System.out.println("[DEV] receiveCashPayment(" + transactionId + ", " + cash + ")");

        // Validate cash
        if (cash <= 0) {
            throw new InvalidPaymentException();
        }

        double returnChange = -1;

        // Validate and retrieve transaction by id
        SaleTransaction transaction = getSaleTransaction(transactionId);

        if (transaction != null && cash >= transaction.getAmount()) {
            transaction.setCash(cash);
            double change = cash - transaction.getAmount();
            transaction.setChange(change);
            transaction.setStatus(SaleTransaction.StatusEnum.PAID);
            transaction.setPaymentType("cash");

            try {
                TransactionManager.callInTransaction(connectionSource,
                        () -> {
                            saleTransactionDao.update(transaction);

                            updateInventoryByPaidTransaction(transaction);

                            BalanceOperation balanceOperation = new BalanceOperation(transaction.getAmount());
                            balanceOperationDao.create(balanceOperation);

                            return null;
                        });

                returnChange = change;
                ongoingTransaction = null;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return returnChange;
    }

    /**
     * This method record the payment of a sale with credit card. If the card has not enough money the payment should
     * be refused.
     * The credit card number validity should be checked. It should follow the luhn algorithm.
     * The credit card should be registered in the system.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the sale that the customer wants to pay
     * @param creditCard    the credit card number of the customer
     * @return true if the operation is successful
     * false   if the sale does not exists,
     * if the card has not enough money,
     * if the card is not registered,
     * if there is some problem with the db connection
     * @throws InvalidTransactionIdException if the sale number is less than or equal to 0 or if it is null
     * @throws InvalidCreditCardException    if the credit card number is empty, null or if luhn algorithm does not
     *                                       validate the credit card
     * @throws UnauthorizedException         if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean receiveCreditCardPayment(Integer transactionId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {

        System.out.println("[DEV] receiveCreditCardPayment(" + transactionId + ", " + creditCard + ")");

        boolean isSuccessful = false;

        // Validate credit card
        if (creditCard == null || creditCard.isEmpty() || !validateCreditCard(creditCard)) {
            throw new InvalidCreditCardException();
        }

        // Validate and retrieve transaction by id
        SaleTransaction transaction = getSaleTransaction(transactionId);

        try {

            // Retrieve credit card
            CreditCard card = creditCardDao.queryForId(creditCard);

            if (card == null || card.getAmount() < transaction.getAmount()) {
                // Card is not registered or has not enough money
                return false;
            }

            transaction.setPaymentType("card");
            transaction.setCreditCard(card);
            transaction.setStatus(SaleTransaction.StatusEnum.PAID);

            TransactionManager.callInTransaction(connectionSource,
                    () -> {
                        saleTransactionDao.update(transaction);

                        updateInventoryByPaidTransaction(transaction);

                        double balanceChange = transaction.getAmount();

                        BalanceOperation balanceOperation = new BalanceOperation(balanceChange);
                        balanceOperationDao.create(balanceOperation);

                        // Reduce credit card balance
                        card.setAmount(card.getAmount() - balanceChange);
                        creditCardDao.update(card);

                        return null;
                    });

            isSuccessful = true;
            this.ongoingTransaction = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isSuccessful;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {

        System.out.println("[DEV] returnCashPayment(" + returnId );

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        double returncash=-1;

        if (returnId == null || returnId<=0) throw new InvalidTransactionIdException();

        ReturnTransaction returnTransaction= getReturnTransaction(returnId);
        if (returnTransaction!=null && returnTransaction.getStatus()==ReturnTransaction.StatusEnum.CLOSED){
            returncash= returnTransaction.getReturnValue();
            returnTransaction.setStatus(ReturnTransaction.StatusEnum.PAID);
            try{
                returnTransactionDao.update(returnTransaction);

                // TODO UPDATE DAILY BALANCEOPERATION

            }catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return returncash;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        System.out.println("[DEV] returnCreditCardPayment(" + returnId );

        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager, User.RoleEnum.Cashier);

        // Validate credit card
        if (creditCard == null || creditCard.isEmpty() || !validateCreditCard(creditCard)) {
            throw new InvalidCreditCardException();
        }

        // TODO CHECK IF CREDIT CARD IS REGISTERED


        double returncash=-1;

        if (returnId == null || returnId<=0) throw new InvalidTransactionIdException();

        ReturnTransaction returnTransaction= getReturnTransaction(returnId);
        if (returnTransaction!=null && returnTransaction.getStatus()==ReturnTransaction.StatusEnum.CLOSED){
            returncash= returnTransaction.getReturnValue();
            returnTransaction.setStatus(ReturnTransaction.StatusEnum.PAID);
            try{
                returnTransactionDao.update(returnTransaction);

                // TODO UPDATE DAILY BALANCEOPERATION

            }catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return returncash;
    }

    /**
     * This method record a balance update. <toBeAdded> can be both positive and negative. If positive the balance entry
     * should be recorded as CREDIT, if negative as DEBIT. The final balance after this operation should always be
     * positive.
     * It can be invoked only after a user with role "Administrator", "ShopManager" is logged in.
     *
     * @param toBeAdded the amount of money (positive or negative) to be added to the current balance. If this value
     *                  is >= 0 than it should be considered as a CREDIT, if it is < 0 as a DEBIT
     * @return true if the balance has been successfully updated
     * false if toBeAdded + currentBalance < 0.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager);

        boolean isUpdated = false;

        double currentBalance = computeBalance();

        try {
            if (currentBalance + toBeAdded >= 0) {
                BalanceOperation balanceOperation = new BalanceOperation(toBeAdded);
                balanceOperationDao.create(balanceOperation);
                isUpdated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUpdated;
    }

    @Override
    public List<it.polito.ezshop.data.BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {

        List<it.polito.ezshop.data.BalanceOperation> balanceList = new ArrayList<>();

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager);

        // get order list
        try {

            if (from == null && to == null) {
                balanceList.addAll(balanceOperationDao.queryForAll());
            } else if (from == null) {
                balanceList.addAll(balanceOperationDao.queryBuilder().where().
                        le("date_string", java.sql.Date.valueOf(to)).query());
            } else if (to == null) {
                balanceList.addAll(balanceOperationDao.queryBuilder().where().
                        ge("date_string", java.sql.Date.valueOf(from)).query());
            } else {
                balanceList.addAll(balanceOperationDao.queryBuilder().where().
                        ge("date_string", java.sql.Date.valueOf(from)).
                        and().le("date_string", java.sql.Date.valueOf(to)).query());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balanceList;


    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.ShopManager);

        double currentBalance = 0.0;

        //currentBalance = balanceOperationDao.queryRawValue("select sum(money) from balance_operations");

        for (int i = 0; i < getCreditsAndDebits(null, null).size(); i++) {
            currentBalance += getCreditsAndDebits(null, null).get(i).getMoney();
        }

        return currentBalance;
    }

    private void authorize(User.RoleEnum... roles) throws UnauthorizedException {
        if (userLogged == null || !Arrays.asList(roles).contains(User.RoleEnum.valueOf(userLogged.getRole()))) {
            throw new UnauthorizedException();
        }
    }

    private String hashPassword(String password) {
        String hashedPassword = null;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            hashedPassword = byteToHex(sha1.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static boolean validateCreditCard(String creditCard) {
        boolean result;
        boolean alternate = false;
        int sum = 0;
        int n;

        // validate through Luhn's algorithm
        for (int i = creditCard.length() - 1; i >= 0; i--) {
            n = Integer.parseInt(creditCard.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n >= 10) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        result = ((sum % 10) == 0);
        return result;
    }

    public static boolean validateBarcode(String barCode) {

        /*The barcode number related to a product type should be a string
         of digits of either 12, 13 or 14 numbers validated following this algorithm
         https://www.gs1.org/services/how-calculate-check-digit-manually
         */

        switch (barCode.length()) {
            case 12:
                barCode = "00" + barCode;
                break;
            case 13:
                barCode = "0" + barCode;
                break;
            case 14:
                break;
            default:
                //wrong number of digits
                return false;
        }

        int sum = 0;
        for (int i = 0; i < barCode.length() - 1; i++) {
            if (i % 2 == 0) {
                sum += Character.getNumericValue(barCode.charAt(i)) * 3;
            } else {
                sum += Character.getNumericValue(barCode.charAt(i));
            }
        }

        int last = Character.getNumericValue(barCode.charAt(barCode.length() - 1));
        int check = (10 - (sum % 10)) % 10;

        return check == last;
    }


    /**
     * Retrieve the ongoing_transaction (taken from this.ongoingTransaction if compatible, else from DB)
     * <p>
     * As a side effect, update this.ongoingTransaction with the last requested transaction
     *
     * @param transactionId The id provided by the GUI method
     * @return the requested SaleTransaction or null
     */
    public SaleTransaction getOngoingTransactionById(Integer transactionId) {

        SaleTransaction returnTransaction = null;

        if (transactionId.equals(ongoingTransaction.getId())) {
            returnTransaction = ongoingTransaction;
        } else {
            try {
                returnTransaction = saleTransactionDao.queryForId(transactionId);

                if (returnTransaction != null) {
                    this.ongoingTransaction = returnTransaction;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return returnTransaction;
    }

    /**
     * Decreases the inventory availability by the sold amount
     *
     * @param transaction The transaction we are updating
     */
    private void updateInventoryByPaidTransaction(SaleTransaction transaction) throws SQLException {
        for (SaleTransactionRecord record : transaction.getRecords()) {
            ProductType product = record.getProductType();
            product.setQuantity(product.getQuantity() - record.getAmount());
            productTypeDao.update(product);
        }

    }

    private void loadCreditCardsFromUtils() throws IOException {

        Stream<String> lines = Files.lines(Paths.get(CREDIT_CARDS_FILE_PATH), Charset.defaultCharset());

        lines.skip(3)
                .forEachOrdered(line -> {
                    if (!line.startsWith("#")) loadCreditCard(line);
                });
    }

    private void loadCreditCard(String line) {
        String[] lineParts = line.split(";");
        CreditCard card = new CreditCard(lineParts[0], Double.parseDouble(lineParts[1]));

        try {
            if(!creditCardDao.idExists(card.getCode()))
            creditCardDao.create(card);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
