package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ClientBasic;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientManagerTest {

    private static ClientBasic client = new ClientBasic("1", "YOU", "NEVER");
    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        new LogManager().saveAccount(client.mail(), "GIVE", true, client.name(), "UP");
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
        DB.getInstance().executeQuery("ALTER TABLE provider_contract AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE wallet_contract AUTO_INCREMENT = 1", false);

    }

    @Test
    @Order(1)
    void getAllClients()
    {

        ClientBasic toBeTested = ((ArrayList<ClientBasic>) new ClientManager().getAllClients(0,1)[1]).get(0);
        assertEquals(toBeTested.clientId(), client.clientId());
        assertEquals(toBeTested.name(), client.name());
        assertEquals(toBeTested.mail(), client.mail());
    }

    @Test
    @Order(2)
    void getAllHisClients()
    {
        ClientBasic toBeTested = ((ArrayList<ClientBasic>) new ClientManager().getAllHisClients("2", 0, 1)[1]).get(0);
        assertEquals(toBeTested.mail(), client.mail());
        assertEquals(toBeTested.name(), client.name());
        assertEquals(toBeTested.clientId(), client.clientId());
    }


    @Test
    @Order(3)
    void deleteClient()
    {
        ClientManager clientManager = new ClientManager();
        clientManager.deleteClient("2","1");
        assertEquals((clientManager.getAllHisClients("1", 0, 1)[1]), Table.EMPTY_TABLE);
    }
}