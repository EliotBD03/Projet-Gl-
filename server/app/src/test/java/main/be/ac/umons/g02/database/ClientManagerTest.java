package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ClientBasic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientManagerTest {

    private static ClientBasic client = new ClientBasic("1", "YOU", "NEVER");
    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        new LogManager().saveAccount(client.getMail(), "GIVE", true, client.getName(), "UP");
        new LogManager().saveAccount("NEVERE", "LET", false, "YOU", "DOWN");
        DB.getInstance().executeQuery("INSERT INTO wallet(address, client_id, wallet_name) VALUES('CECIESTUNERUE',1,'non')",false);
        DB.getInstance().executeQuery("INSERT INTO wallet_contract(address) VALUES('CECIESTUNERUE')", false);
        DB.getInstance().executeQuery("INSERT INTO provider_contract(provider_id) VALUES(2)",false);
    }

    @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("DELETE FROM wallet_contract",false);
        DB.getInstance().executeQuery("DELETE FROM wallet",false);
        DB.getInstance().executeQuery("DELETE FROM provider_contract",false);
        new LogManager().deleteAccount("1");
        new LogManager().deleteAccount("2");
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    void getAllClients()
    {

        ClientBasic toBeTested = new ClientManager().getAllClients(0,1, "").get(0);
        assertEquals(toBeTested.getClientId(), client.getClientId());
        assertEquals(toBeTested.getName(), client.getName());
        assertEquals(toBeTested.getMail(), client.getMail());
    }

    @Test
    void getAllHisClients()
    {
        ClientBasic toBeTested = new ClientManager().getAllHisClients("2", 0, 1, "").get(0);
        assertEquals(toBeTested.getMail(), client.getMail());
        assertEquals(toBeTested.getName(), client.getName());
        assertEquals(toBeTested.getClientId(), client.getClientId());
    }


    @Test
    void deleteClient()
    {
        ClientManager clientManager = new ClientManager();
        clientManager.deleteClient("2","1");
        assertEquals(clientManager.getAllHisClients("1", 0, 1, "").size(), 0);
    }
}