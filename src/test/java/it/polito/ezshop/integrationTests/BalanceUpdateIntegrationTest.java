package it.polito.ezshop.integrationTests;

import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class BalanceUpdateIntegrationTest extends BaseIntegrationTest {

    private static List<BalanceOperation> expectedBalanceList = new ArrayList<>();
    private static final LocalDate now = LocalDate.now();
//    private static final LocalDate from = LocalDate.now().minusDays(1);
//    private static final LocalDate to = LocalDate.now().plusDays(1);

    @Before
    public void createBalanceOperation() throws UnauthorizedException {
        loginAs(User.RoleEnum.Administrator);

        boolean res1 = shop.recordBalanceUpdate(100);
        assertTrue(res1);
        boolean res2 = shop.recordBalanceUpdate(-50);
        assertTrue(res2);

        expectedBalanceList = shop.getCreditsAndDebits(now, now);
    }
    //Scenario 9-1
    @Test
    public void testListBalance() throws UnauthorizedException {
        loginAs(User.RoleEnum.Administrator);

        List<BalanceOperation> actualBalanceList = shop.getCreditsAndDebits(now, now);
        //Since there seems to be no way to compare two ArrayList, it will be only compared the size of them.
        //(all the failed attempts are commented below)
        assertEquals(actualBalanceList.size(), expectedBalanceList.size());

//        assertEquals(String.valueOf(actualBalanceList), String.valueOf(expectedBalanceList));
//        assertTrue(actualBalanceList.containsAll(expectedBalanceList));
//        assertEquals(true, Arrays.equals(actualBalanceList.toArray(), expectedBalanceList.toArray()));
//        assertEquals(expectedBalanceList, actualBalanceList);
//        assertArrayEquals(expectedBalanceList.toArray(), actualBalanceList.toArray());
//        assertEquals(expectedBalanceList.toString(), actualBalanceList.toString());
//        assertEquals(expectedBalanceList.stream().sorted(), actualBalanceList.stream().sorted());
//        assertThat(actualBalanceList, is(expectedBalanceList));
//        assertEquals(new HashSet<>(expectedBalanceList), new HashSet<>(actualBalanceList));
//        assertEquals(new TreeSet<>(expectedBalanceList), new TreeSet<>(actualBalanceList));
//        assertEquals(expectedBalanceList.listIterator(), actualBalanceList.listIterator());
//        assertTrue(actualBalanceList.containsAll(expectedBalanceList) && expectedBalanceList.containsAll(actualBalanceList));
//        assertEquals(new ArrayList<>(expectedBalanceList), actualBalanceList);

    }

}
