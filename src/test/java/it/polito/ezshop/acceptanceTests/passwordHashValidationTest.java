package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

public class passwordHashValidationTest {

    @Test
    public void testValidByteToHexString(){
        String methodName = "testValidByteToHexString";
        String correctMethodNameEncoding = "7465737456616c696442797465546f486578537472696e67";

        byte[] hexMethodName = methodName.getBytes(StandardCharsets.UTF_8);
        String encodedName = EZShop.byteToHex(hexMethodName);

        assertEquals(encodedName, correctMethodNameEncoding);
    }

    @Test
    public void testStandardSHA1Implementation(){
        String password = "password";
        String correctSHA1Password = "5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8";
        String correctCleartextPassword = "password";

        assertEquals(correctCleartextPassword, EZShop.hashPassword(password));
    }
}
