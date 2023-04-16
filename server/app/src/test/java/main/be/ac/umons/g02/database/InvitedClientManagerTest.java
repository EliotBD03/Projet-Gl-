//Extension Claire
/*package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.InvitedClient;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvitedClientManagerTest
{
    private static InvitedClient client = new InvitedClient("R", "name1", "1","mail1");
    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        LogManager logManager = new LogManager();
        //On suppose que les tables sont vides donc par défaut name1 -> id = 1 et name2 -> id = 2 
        logManager.saveAccount("mail1", "password", true, "name1", "english");
        logManager.saveAccount("mail2", "password", true, "name2", "english");
    }

    @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("DELETE FROM invitedTable",false);
        DB.getInstance().executeQuery("DELETE FROM user",false);
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    @Order(1)
    void addInvited()
    {
        assertEquals(new InvitedClientManager().addInvited("2", "address", "1", "R"), true);
        /*DB.getInstance().executeQuery("SELECT * FROM invitedTable", true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults("address", "invitedId", "ownerId", "permission");
        assertEquals("address", results.get(0).get(0));
        assertEquals("1", results.get(1).get(0));
        assertEquals("2", results.get(2).get(0));
        assertEquals("R", results.get(3).get(0));
    }

    @Test
    @Order(2)
    void getAllInvited()
    {
        InvitedClient toBeTested = ((ArrayList<InvitedClient>) new InvitedClientManager().getAllInvitedClients("address", 0, 1)[1]).get(0);
        assertEquals(client.permission(), toBeTested.permission());
        assertEquals(client.invitedName(), toBeTested.invitedName());
        assertEquals(client.invitedId(), toBeTested.invitedId());
        assertEquals(client.invitedMail(), toBeTested.invitedMail());
    }

    @Test
    @Order(3)
    void changePerm()
    {
        new InvitedClientManager().changePermission("address", "1","RW");
        DB.getInstance().executeQuery("SELECT * FROM invitedTable", true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"permission"});
        assertEquals("RW", results.get(3).get(0));
    }

    @Test
    @Order(4)
    void deleteInvited()
    {
        new InvitedClientManager().deleteInvitedClient("address", "1");
        DB.getInstance().executeQuery("SELECT * FROM invitedTable", true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"address", "invitedId", "ownerId", "permission"});
        assertEquals(results.get(0).size(), 0);
    }
}*/