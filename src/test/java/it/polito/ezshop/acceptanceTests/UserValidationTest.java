package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.model.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserValidationTest {

    @Test
    public void testParametricConstructor(){
        String testUsername = "mario.rossi";
        String testPassword = "anypassword";
        User.RoleEnum testRoleEnum = User.RoleEnum.Cashier;

        User user = new User(testUsername, testPassword, testRoleEnum);

        assertEquals(user.getUsername(), testUsername);
        assertEquals(user.getPassword(), testPassword);
        assertEquals(user.getRole(), testRoleEnum.toString());
    }

    @Test
    public void testSetId() {
        Integer testId = 42;
        User user = new User();
        user.setId(testId);

        assertEquals(testId, user.getId());
    }

    @Test
    public void testSetUsername() {
        String testUsername = "mario.rossi";

        User user = new User();
        user.setUsername(testUsername);

        assertEquals(testUsername, user.getUsername());
    }

    @Test
    public void testSetPassword() {
        String testPassword = "anypassword";

        User user = new User();
        user.setPassword(testPassword);

        assertEquals(testPassword, user.getPassword());
    }

    @Test
    public void testSetRole() {
        String testRole = User.RoleEnum.Cashier.toString();

        User user = new User();
        user.setRole(testRole);

        assertEquals(testRole, user.getRole());
    }

    @Test
    public void testSetRoleEnum() {
        User.RoleEnum testRoleEnum = User.RoleEnum.Cashier;

        User user = new User();
        user.setRole(testRoleEnum);

        assertEquals(testRoleEnum.toString(), user.getRole());
    }

}
