package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

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
        System.out.println("1");
        assertDoesNotThrow(() -> {new LogManager().saveAccount("test@gmail.com", "password", true, "testName","english");});
        assertThrows(Exception.class,() -> {new LogManager().saveAccount("test@gmail.com", "password", true, "testName","english");});
    }

    @Test
    @Order(2)
    void isClient()
    {
        System.out.println("2");
        assertTrue(new LogManager().isClient("1"));
    }

    @Test
    @Order(3)
    void checkAccountTest()
    {
        System.out.println("3");
        assertEquals(new LogManager().checkAccount("test@gmail.com", "password"), "1");
        assertNull(new LogManager().checkAccount("test@gmail.com", "mdp"));
    }

    @Test
    @Order(4)
    void changePasswordTest()
    {
        System.out.println("4");
        assertDoesNotThrow(() -> {new LogManager().changePassword("test@gmail.com", "newPassword");});
    }

    @Test
    @Order(5)
    void getName()
    {
        System.out.println("5");
        assertEquals("testName", new LogManager().getName("1"));
    }

    @Test
    @Order(6)
    void deleteAccount()
    {
        System.out.println("6");
        new LogManager().deleteAccount("1");
        assertNull(new LogManager().checkAccount("test@gmail.com", "password"));
    }

}