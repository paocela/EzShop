package it.polito.ezshop.acceptanceTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        creditCardValidationTest.class,
        barCodeValidationTest.class,
        SaleTransactionRecordValidationTest.class,
        passwordHashValidationTest.class,
        CustomerValidationTest.class,
        orderTest.class,
        balanceOperationTest.class,
        UserValidationTest.class,
        SaleTransactionValidationTest.class
})


public class TestEZShop {

}
