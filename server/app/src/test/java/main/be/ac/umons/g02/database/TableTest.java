package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
public class TableTest
{
    @BeforeAll
    static void setUp()
    {
        DBTest.setUp();
        DB.getInstance().executeQuery("INSERT INTO user(mail, password, name) VALUES('test@gmail.com', 'testPassword', 'testName')", false);
        DB.getInstance().executeQuery("INSERT INTO user(mail, password, name) VALUES('test', 'test', 'test')", false);
    }

    @AfterAll
    static void clean()
    {
        DBTest.clean();
    }

    @Test
    void IsWellTransposed() throws Exception
    {
        Table table = new Table(new Query("SELECT * FROM user"), "mail", "password", "name");
        ArrayList<ArrayList<String>> result = table.getTable();
        assertEquals(result.get(0).get(0), "test@gmail.com");
        assertEquals(result.get(1).get(0), "test");
    }
}
