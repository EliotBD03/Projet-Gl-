package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogManagerTest {


    @Test
    void saveAccountTest()
    {
        //we suppose that the database doesn't contain these values
        assertDoesNotThrow(() -> {new LogManager().saveAccount("test@gmail.com", "password", true, "testname","english");});
        assertThrows(Exception.class,() -> {new LogManager().saveAccount("test@gmail.com", "password", true, "testname","english");});
    }

    @Test
    void checkAccountTest()
    {
        assertTrue(new LogManager().checkAccount("test@gmail.com", "password"));
        assertFalse(new LogManager().checkAccount("test@gmail.com", "mdp"));
    }

    @Test
    void changePasswordTest()
    {
        assertDoesNotThrow(() -> {new LogManager().changePassword("test@gmail.com", "newPassword");});
    }

}