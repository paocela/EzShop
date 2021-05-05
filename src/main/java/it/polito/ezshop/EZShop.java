package it.polito.ezshop;

import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.view.EZShopGUI;


public class EZShop {

    public static void main(String[] args){
        EZShopInterface ezShop = new it.polito.ezshop.data.EZShop();
        EZShopGUI gui = new EZShopGUI(ezShop);
        try {
            ezShop.createUser("luca", "password", "Administrator");
        } catch (InvalidUsernameException | InvalidPasswordException | InvalidRoleException e) {
            e.printStackTrace();
        }
    }

}
