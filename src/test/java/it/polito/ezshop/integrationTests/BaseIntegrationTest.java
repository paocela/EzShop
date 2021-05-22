package it.polito.ezshop.integrationTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class BaseIntegrationTest {

    static final EZShop shop = new EZShop(true);

    static void loginAs(it.polito.ezshop.model.User.RoleEnum role) {
        User user = null;
        try{
            switch (role){
                case Administrator:
                    user = shop.login("testAdministrator", "password");
                    break;
                case ShopManager:
                    user = shop.login("testShopManager", "password");
                    break;
                case Cashier:
                    user = shop.login("testCashier", "password");
                    break;
            }
        } catch (InvalidPasswordException | InvalidUsernameException e) {
            // This should never happen as these credentials are embedded into the test definition
            e.printStackTrace();
        }

        assertNotNull(user);
        assertEquals(user.getRole(), role.toString());
    }
}
