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
import it.polito.ezshop.model.Customer;

public class EZShop implements EZShopInterface {

    private final static String DATABASE_URL = "jdbc:sqlite:data/db.sqlite";

    ConnectionSource connectionSource;
    Dao<User, Integer> userDao;
    Dao<ProductType, Integer> productTypeDao;
    Dao<Customer, Integer> customerDao;

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
            TableUtils.createTableIfNotExists(connectionSource, Customer.class);

            userDao = DaoManager.createDao(connectionSource, User.class);
            productTypeDao = DaoManager.createDao(connectionSource, ProductType.class);
            customerDao = DaoManager.createDao(connectionSource, Customer.class);

        } catch (SQLException e) {
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
            setUserLogged(returnUser);

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

        List<it.polito.ezshop.data.ProductType> products= null;
        try {
            products= new ArrayList<>(productTypeDao.queryForAll());
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

        List<it.polito.ezshop.data.ProductType> products= null;

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
     *
     * @return  true if the update was successful
     *          false if the product does not exists, if <toBeAdded> is negative and the resulting amount would be
     *          negative too or if the product type has not an assigned location.
     *
     * @throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
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
     * @param newPos the new position the product should be placed to.
     *
     * @return true if the update was successful
     *          false if the product does not exists or if <newPos> is already assigned to another product
     *
     * @throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
     * @throws InvalidLocationException if the product location is in an invalid format (not <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>)
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
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
        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        Integer customerId = -1;

        // verify customer name validity
        if (customerName == null || customerName.isEmpty()) {
            throw new InvalidCustomerNameException();
        }

        // verify if logged in user
        if (getUserLogged() == null) {
            throw new UnauthorizedException();
        }

        // create customer in db
        try {
            QueryBuilder<Customer, Integer> customerQueryBuilder = customerDao.queryBuilder();
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
        } else if (newCustomerCard.length() == 0) {
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

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        return false;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        Customer returnCustomer = null;

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        // verify id validity
        if (id == null || id < 1) {
            throw new InvalidCustomerIdException();
        }

        // verify if logged in user
        if (getUserLogged() == null) {
            throw new UnauthorizedException();
        }

        // get customer by id
        try {
            returnCustomer = customerDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnCustomer;
    }

    @Override
    public List<it.polito.ezshop.data.Customer> getAllCustomers() throws UnauthorizedException {
        List<it.polito.ezshop.data.Customer> customerList = new ArrayList<>();

        // check privileges
        authorize(User.RoleEnum.Administrator, User.RoleEnum.Cashier, User.RoleEnum.ShopManager);

        // verify if logged in user

        if (getUserLogged() == null) {
            throw new UnauthorizedException();
        }
        // get customer list
        try {
            customerList.addAll(customerDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
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

    private boolean validateBarcode(String barCode){
        // TODO IMPLEMENT
        return true;
    }
}
