package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * We suppose that the tests are executed with an empty database.
 */
class LogManagerTest {

    @BeforeAll
    static void setUp()
    {
        DBTest.setUp();
    }

    @Test
    void saveAccountTest()
    {
        assertDoesNotThrow(() -> {new LogManager().saveAccount("test@gmail.com", "password", true, "testName","english");});
        assertThrows(Exception.class,() -> {new LogManager().saveAccount("test@gmail.com", "password", true, "testName","english");});
    }

    @Test
    void deleteAccount()
    {
        new LogManager().deleteAccount("1");
        assertNull(new LogManager().checkAccount("test@gmail.com", "password"));
    }

    @Test
    void isClient()
    {
        assertTrue(new LogManager().isClient("1"));
    }

    @Test
    void checkAccountTest()
    {
        assertEquals(new LogManager().checkAccount("test@gmail.com", "password"), "1");
        assertNull(new LogManager().checkAccount("test@gmail.com", "mdp"));
    }

    @Test
    void changePasswordTest()
    {
        assertDoesNotThrow(() -> {new LogManager().changePassword("test@gmail.com", "newPassword");});
    }

    @Test
    void getName()
    {
        assertEquals("testName", new LogManager().getName("1"));
    }


}