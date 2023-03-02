package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DBTest {

    private static final String userName = "297895";
    private static final String password = "walletbaba_great";
    private static final String dataBaseName = "babawallet_db_test";
    @BeforeAll
    static void setUp()
    {
        DB.setUserName(userName);
        DB.setDataBaseName(dataBaseName);
        DB.setPassword(password);
    }

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
        DB.getInstance().executeQuery("INSERT INTO user(mail, password, name) VALUES('test@gmail.com', 'testPassword', 'testName')",false);
        String query = "SELECT name FROM user WHERE password='testPassword'";
        db.executeQuery(query, true);
        assertEquals(db.getResults(new String[] {"name"}).get(0).get(0), "testName");
        DB.getInstance().executeQuery("DELETE FROM user WHERE mail='test@gmail.com'",false);
        db.executeQuery("SELECT * FROM user WHERE name='testName'",true);
        assertEquals(0, db.getResults(new String[]{"name"}).get(0).size());
    }
}