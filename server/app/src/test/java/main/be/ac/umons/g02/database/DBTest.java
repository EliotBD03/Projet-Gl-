package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DBTest {

    @Test
    void getInstance()
    {
        assertNotNull(DB.getInstance().getClass());
    }

    @Test
    void executeQuery()
    {
        assertTrue(DB.getInstance().executeQuery("SELECT * FROM client", true));
    }

    @Test
    void getResults()
    {
        DB db = DB.getInstance();
        String query = "SELECT name FROM user WHERE password='test'";
        db.executeQuery(query, true);
        assertEquals(db.getResults(new String[] {"name"}).get(0).get(0), "test");
    }
}