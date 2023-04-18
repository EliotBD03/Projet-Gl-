//Extension Claire
package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Invitation;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvitationManagerTest
{
    private static Invitation propose = new Invitation("1", "1", "2","address", "RW", "client1", "request");
    private static Invitation propose2 = new Invitation("3", "1", "2","address2", "R", "client1", "request");
    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        LogManager logManager = new LogManager();
        logManager.saveAccount("clientmail", "password", true, "client1", "english");
        logManager.saveAccount("clientmail2", "password", true, "client2", "english");
    }

    @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("TRUNCATE TABLE invitation",false);
        DB.getInstance().executeQuery("DELETE FROM invitedTable",false);
        DB.getInstance().executeQuery("DELETE FROM client",false);
        DB.getInstance().executeQuery("DELETE FROM user",false);
        DB.getInstance().executeQuery("ALTER TABLE invitation AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);

    }

    @Test
    @Order(1)
    void createInvitation()
    {
        assertTrue(new InvitationManager().createInvitation(propose.senderId(), propose.receiverId(), propose.address(), propose.permission(), propose.nameSender(), propose.type()));
        DB.getInstance().executeQuery("SELECT * FROM invitation", true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults("senderId", "receiverId", "address", "permission", "nameSender", "type");
        assertEquals(propose.senderId(), results.get(0).get(0));
        assertEquals(propose.receiverId(), results.get(1).get(0));
        assertEquals(propose.address(), results.get(2).get(0));
        assertEquals(propose.permission(), results.get(3).get(0));
        assertEquals(propose.nameSender(), results.get(4).get(0));
        assertEquals(propose.type(), results.get(5).get(0));
        assertFalse(new InvitationManager().createInvitation(propose.senderId(), propose.senderId(), propose.address(), propose.permission(), propose.nameSender(), propose.type()));
        assertFalse(new InvitationManager().createInvitation(propose.senderId(), '20', propose.address(), propose.permission(), propose.nameSender(), propose.type()));

    }

    @Test
    @Order(2)
    void getAllInvitations()
    {
        Invitation toBeTested = ((ArrayList<Invitation>) new InvitationManager().getAllInvitation("2", 0, 1)[1]).get(0);
        assertEquals(propose.invitationId(), toBeTested.invitationId());
        assertEquals(propose.senderId(), toBeTested.senderId());
        assertEquals(propose.receiverId(), toBeTested.receiverId());
        assertEquals(propose.address(), toBeTested.address());
        assertEquals(propose.permission(), toBeTested.permission());
        assertEquals(propose.nameSender(), toBeTested.nameSender());
        assertEquals(propose.type(), toBeTested.type());
    }

    @Test
    @Order(3)
    void acceptInvitation()
    {
        InvitationManager invitation = new InvitationManager();
        invitation.acceptInvitation(propose.invitationId());

        DB.getInstance().executeQuery("SELECT * FROM invitation WHERE receiverId=1",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"senderId", "receiverId", "address", "permission", "type"});
        assertEquals(propose.senderId(), results.get(1).get(0));
        assertEquals(propose.receiverId(), results.get(0).get(0));
        assertEquals(propose.address(), results.get(2).get(0));
        assertEquals(propose.permission(), results.get(3).get(0));
        assertEquals("accept", results.get(4).get(0));
    }

    @Test
    @Order(4)
    void deleteInvitation()
    {
        InvitationManager invitationManager = new InvitationManager();
        invitationManager.deleteInvitation("2");
        DB.getInstance().executeQuery("SELECT * FROM invitation WHERE invitationId=2",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"senderId", "receiverId", "address", "permission", "nameSender", "type"});
        assertEquals(results.get(0).size(), 0);
    }

    @Test
    @Order(5)
    void refuseInvitation()
    {
        new InvitationManager().createInvitation(propose2.senderId(), propose2.receiverId(), propose2.address(), propose2.permission(), propose2.nameSender(), propose2.type());
        InvitationManager invitation = new InvitationManager();
        invitation.refuseInvitation(propose2.invitationId());

        DB.getInstance().executeQuery("SELECT * FROM invitation WHERE receiverId=1",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"senderId", "receiverId", "address", "permission", "type"});
        assertEquals(propose2.senderId(), results.get(1).get(0));
        assertEquals(propose2.receiverId(), results.get(0).get(0));
        assertEquals(propose2.address(), results.get(2).get(0));
        assertEquals(propose2.permission(), results.get(3).get(0));
        assertEquals("denial", results.get(4).get(0));
    }
}