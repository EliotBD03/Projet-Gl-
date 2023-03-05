package main.be.ac.umons.g02.database;

import com.mysql.cj.log.Log;
import main.be.ac.umons.g02.data_object.ClientBasic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientManagerTest {

    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        new LogManager().saveAccount("NEVER", "GIVE", true, "YOU", "UP");
        new LogManager().saveAccount("NEVERE", "LET", false, "YOU", "DOWN");

    }

    @AfterAll
    static void clean()
    {
        new LogManager().deleteAccount("1");
        new LogManager().deleteAccount("2");
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    void getAllClients()
    {
        ClientBasic expected = new ClientBasic("1", "YOU", "NEVER");
        ClientBasic toBeTested = new ClientManager().getAllClients(0,1).get(0);
        assertEquals(toBeTested.getClientId(), expected.getClientId());
        assertEquals(toBeTested.getName(),expected.getName());
        assertEquals(toBeTested.getMail(), expected.getMail());
    }

    @Test
    void getAllHisClients()
    {
        //TODO do it when finished proposalManagerTest
    }


    @Test
    void deleteClient()
    {
    }
}