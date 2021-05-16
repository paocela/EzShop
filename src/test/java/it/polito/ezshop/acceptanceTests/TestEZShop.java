package it.polito.ezshop.acceptanceTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        creditCardValidationTest.class, barCodeValidationTest.class, saleTransactionRecordValidationTest.class, refreshAmountTest.class
})


public class TestEZShop {

}
