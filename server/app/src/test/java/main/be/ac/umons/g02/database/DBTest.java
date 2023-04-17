package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DBTest {

    @BeforeAll
    static void setUp()
    {
        DB.setDataBaseName("NAMEDBTEST");
    }

    @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("DELETE FROM user", false);
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    void doesTheEnvironmentVariableExist()
    {
        assertTrue(System.getenv().containsKey("NAMEDBTEST"));
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
    }
}