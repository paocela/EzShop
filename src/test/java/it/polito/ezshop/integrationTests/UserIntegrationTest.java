package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
public class UserIntegrationTest extends BaseIntegrationTest {
    static Integer userId;
    static Integer userId2;

    @BeforeClass
    public static void init() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException {
        loginAs(User.RoleEnum.ShopManager);
        userId = shop.createUser("newUser", "password", "Cashier");
    }

    @Test
    public void testCreateUser() {
        loginAs(User.RoleEnum.ShopManager);

        assertTrue(userId > 0);
    }

    @Test
    public void testGetUser() throws InvalidUserIdException, UnauthorizedException {
        loginAs(User.RoleEnum.Administrator);

        assertEquals(userId, shop.getUser(userId).getId());
    }

    @Test
    public void testGetAllUsers() throws UnauthorizedException, InvalidPasswordException, InvalidRoleException, InvalidUsernameException {
        loginAs(User.RoleEnum.Administrator);

        userId2 = shop.createUser("newUser2", "password", "Cashier");
        assertTrue(userId2 > 0);
        List<it.polito.ezshop.data.User> list = shop.getAllUsers();
        assertTrue(list.stream().map(x -> x.getId()).collect(Collectors.toList()).contains(userId));
        assertTrue(list.stream().map(x -> x.getId()).collect(Collectors.toList()).contains(userId2));
    }

    @Test
    public void testModifyUserRights() throws InvalidUserIdException, UnauthorizedException, InvalidRoleException {
        loginAs(User.RoleEnum.Administrator);

        shop.updateUserRights(userId, "Administrator");
        assertEquals("Administrator", shop.getUser(userId).getRole());
        shop.updateUserRights(userId, "Cashier");
        assertEquals("Cashier", shop.getUser(userId).getRole());
        shop.updateUserRights(userId, "ShopManager");
        assertEquals("ShopManager", shop.getUser(userId).getRole());
    }

    @AfterClass
    public static void testDeleteUser() throws InvalidUserIdException, UnauthorizedException {
        loginAs(User.RoleEnum.Administrator);

        assertTrue(shop.deleteUser(userId));
        assertTrue(shop.deleteUser(userId2));
    }
}
