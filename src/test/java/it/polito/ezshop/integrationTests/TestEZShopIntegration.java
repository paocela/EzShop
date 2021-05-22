package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.ArrayList;
import java.util.List;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CustomerIntegrationTest.class
})

public class TestEZShopIntegration extends BaseIntegrationTest {

    private static final List<Integer> testUserIds = new ArrayList<>();

    @BeforeClass
    public static void init() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException {
        Integer testUserId;

        testUserId = shop.createUser("testCashier", "password", "Cashier");
        testUserIds.add(testUserId);

        testUserId = shop.createUser("testShopManager", "password", "ShopManager");
        testUserIds.add(testUserId);

        testUserId = shop.createUser("testAdministrator", "password", "Administrator");
        testUserIds.add(testUserId);
    }


    @AfterClass
    public static void cleanup() throws UnauthorizedException {
        loginAs(User.RoleEnum.Administrator);
        for(Integer id: testUserIds){
            try {
                shop.deleteUser(id);
            } catch (InvalidUserIdException e) {
                // This should never happen as I am deleting the users I created
                e.printStackTrace();
            }
        }
    }
}