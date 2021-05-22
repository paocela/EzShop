package it.polito.ezshop.integrationTests;

import it.polito.ezshop.model.User;
import it.polito.ezshop.exceptions.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticateIntegrationTest extends BaseIntegrationTest {
    static Integer userId;

    @BeforeClass
    public static void init() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException {
        loginAs(User.RoleEnum.Administrator);
        userId = shop.createUser("newUser", "password", "Administrator");
        assertTrue(userId > 0);
    }

    @Test
    public void testValidLogin() throws InvalidPasswordException, InvalidUsernameException {
        loginAs(User.RoleEnum.Administrator);

        User u;
        u = (User) shop.login("newUser", "password");
        assertEquals(userId, u.getId());
    }

    @Test
    public void testValidLogout() {
        loginAs(User.RoleEnum.Administrator);

        assertTrue(shop.logout());
    }

    @AfterClass
    public static void cleanUp() throws InvalidUserIdException, UnauthorizedException {
        loginAs(User.RoleEnum.Administrator);

        shop.deleteUser(userId);
    }
}
