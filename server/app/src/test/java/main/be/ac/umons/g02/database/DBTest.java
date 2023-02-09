package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DBTest {

    @Test
    void getInstance()
    {
        assertEquals(DB.getInstance().getClass(), DB.class);
    }

    @Test
    void isConnectionEstablished()
    {
        assertTrue(DB.getInstance().isConnectionEstablished());
    }

    @Test
    void executeQuery()
    {
        assertTrue(DB.getInstance().executeQuery("SELECT * FROM test"));
    }
}