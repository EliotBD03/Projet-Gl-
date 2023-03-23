package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * We suppose that the tests are executed with an empty database.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LogManagerTest {

    @BeforeAll
    static void setUp()
    {
        DBTest.setUp();
    }

   @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("DELETE FROM client", false);
        DB.getInstance().executeQuery("DELETE FROM language", false);
        DBTest.clean();
    }

    @Test
    @Order(1)
    void saveAccountTest()
    {
        assertDoesNotThrow(() -> {new LogManager().saveAccount("test@gmail.com", "password", true, "testName","english");});
        assertThrows(Exception.class,() -> {new LogManager().saveAccount("test@gmail.com", "password", true, "testName","english");});
    }

    @Test
    @Order(2)
    void isClient()
    {
        assertTrue(new LogManager().isClient("1"));
    }

    @Test
    @Order(3)
    void checkAccountTest()
    {
        assertEquals(new LogManager().checkAccount("test@gmail.com", "password"), "1");
        assertNull(new LogManager().checkAccount("test@gmail.com", "mdp"));
    }

    @Test
    @Order(4)
    void changePasswordTest()
    {
        assertDoesNotThrow(() -> {new LogManager().changePassword("1", "newPassword");});
        DB.getInstance().executeQuery("SELECT password FROM user WHERE mail='test@gmail.com'",true);
        String password = DB.getInstance().getResults(new String[] {"password"}).get(0).get(0);
        assertTrue(BCrypt.checkpw("newPassword", password));
    }

    @Test
    @Order(5)
    void getName()
    {
        assertEquals("testName", new LogManager().getName("1"));
    }

   @Test
    @Order(6)
    void deleteAccount()
    {
        new LogManager().deleteAccount("1");
        assertNull(new LogManager().checkAccount("test@gmail.com", "password"));
    }

}