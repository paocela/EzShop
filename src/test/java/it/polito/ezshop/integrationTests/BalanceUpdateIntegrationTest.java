package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class BalanceUpdateIntegrationTest extends BaseIntegrationTest {

    private static List<it.polito.ezshop.data.BalanceOperation> expectedBalanceList = new ArrayList<>();
    private static final LocalDate from = LocalDate.now().minusDays(1);
    private static final LocalDate to = LocalDate.now().plusDays(1);

    @BeforeClass
    public static void createBalanceOperation() throws UnauthorizedException {
        loginAs(User.RoleEnum.Administrator);

        boolean res1 = shop.recordBalanceUpdate(100);
        assertTrue(res1);

        expectedBalanceList = shop.getCreditsAndDebits(from, to);
    }

    @Test
    public void testListBalance() throws UnauthorizedException {
        loginAs(User.RoleEnum.Administrator);

        assertEquals(expectedBalanceList, shop.getCreditsAndDebits(from, to));
    }

}
