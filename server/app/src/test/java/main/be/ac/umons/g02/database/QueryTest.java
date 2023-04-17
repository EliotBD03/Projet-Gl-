package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
public class QueryTest
{
    @BeforeAll
    static void setUp()
    {
        DBTest.setUp();
        DB.getInstance().executeQuery("INSERT INTO user(mail, password, name) VALUES('mail', 'password', 'name')",false);
    }

    @AfterAll
    static void clean()
    {
        DBTest.clean();
    }

    @Test
    void executeAndGetResult()
    {
        assertThrows(IllegalArgumentException.class, () -> {new Query("select name from user").executeAndGetResult();});
        ArrayList<ArrayList<String>> expected = new ArrayList<>();
        expected.add(new ArrayList<>(){{add("name");}});
        assertEquals(expected.get(0).get(0), new Query("select name from user").executeAndGetResult("name").getStringElem(0,0));
    }

}
