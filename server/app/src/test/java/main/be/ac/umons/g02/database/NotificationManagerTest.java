package main.be.ac.umons.g02.database;


import main.be.ac.umons.g02.data_object.Notification;
import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.WalletBasic;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NotificationManagerTest
{
    private static Notification createdContract = new Notification("1", "1", "2", "1", "elec","2","contract created", "785","address",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
    private static Notification askingContract = new Notification("2", "1", "2", null, "1", "2","contract request", "785", "address",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
    @BeforeAll
    static void setUp()
    {
        DBTest.setUp();
    }

    @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("TRUNCATE TABLE notification",false);
        DB.getInstance().executeQuery("DELETE FROM counter",false);
        DB.getInstance().executeQuery("DELETE FROM contract",false);
        DB.getInstance().executeQuery("DELETE FROM proposal",false);
        DB.getInstance().executeQuery("DELETE FROM provider_contract",false);
        DB.getInstance().executeQuery("DELETE FROM provider",false);
        DB.getInstance().executeQuery("DELETE FROM wallet_contract",false);
        DB.getInstance().executeQuery("DELETE FROM wallet",false);
        DB.getInstance().executeQuery("DELETE FROM client",false);
        DB.getInstance().executeQuery("DELETE FROM language",false);
        DB.getInstance().executeQuery("DELETE FROM user",false);
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE contract AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE wallet_contract AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE provider_contract AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE notification AUTO_INCREMENT = 1", false);

    }

    @Test
    @Order(1)
    void createNotification()
    {
        new NotificationManager().createNotification(createdContract.senderId(), createdContract.receiverId(), createdContract.proposalName(), createdContract.providerProposalId(), createdContract.context(), createdContract.ean(), createdContract.address());
        DB.getInstance().executeQuery("SELECT * FROM notification", true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name","provider_id_proposal", "context", "linked_ean", "linked_address", "creation_date"});
        assertEquals(createdContract.creationDate(), results.get(7).get(0)); //be careful, can be false for the time between the creation and the reception of the notif
        assertEquals(createdContract.senderId(), results.get(0).get(0));
        assertEquals(createdContract.receiverId(), results.get(1).get(0));
        assertEquals("elec", results.get(2).get(0));
        assertEquals("2", results.get(3).get(0));
        assertEquals(createdContract.context(), results.get(4).get(0));
        assertEquals("785", results.get(5).get(0));
        assertEquals("address", results.get(6).get(0));
    }

    @Test
    @Order(2)
    void getAllNotifications()
    {
        Notification toBeTested = ((ArrayList<Notification>) new NotificationManager().getAllNotifications("2", 0, 1)[1]).get(0);
        assertEquals(createdContract.creationDate(), toBeTested.creationDate());
        assertEquals(createdContract.notificationId(), toBeTested.notificationId());
        assertEquals(createdContract.context(), toBeTested.context());
        assertEquals(createdContract.receiverId(), toBeTested.receiverId());
        assertEquals(createdContract.senderId(), toBeTested.senderId());
        assertEquals(createdContract.providerProposalId(), toBeTested.providerProposalId());
    }

    @Test
    @Order(3)
    void acceptNotification() throws Exception
    {
        NotificationManager notification = new NotificationManager();
        notification.createNotification(askingContract.senderId(), askingContract.receiverId(), "elect", askingContract.senderId(), askingContract.context(), "875", "add");

        LogManager logManager = new LogManager();
        logManager.saveAccount("clientmail", "password", true, "client", "english");
        logManager.saveAccount("providermail", "password", false, "provider", "english");

        new ProposalManager().addProposal(new ProposalFull("2", "provider", "electricity", "100", "elect"));
        new WalletManager().createWallet(new WalletBasic("add", "wallet", "1", "client"));

        notification.acceptNotification("2", "785", "add");
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id=3",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal", "context"});
        assertEquals(askingContract.receiverId(), results.get(0).get(0));
        assertEquals(askingContract.senderId(), results.get(1).get(0));
        assertEquals("elect", results.get(2).get(0));
        assertEquals(askingContract.senderId(), results.get(3).get(0));
        assertEquals("Your contract request was accepted by provider", results.get(4).get(0));


    }

    @Test
    @Order(4)
    void deleteNotification()
    {
        NotificationManager notificationManager = new NotificationManager();
        notificationManager.deleteNotification("0");
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id=0",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal", "context"});
        assertEquals(results.get(0).size(), 0);
    }

    @Test
    @Order(5)
    void refuseNotification()
    {
        NotificationManager notification = new NotificationManager();
        notification.createNotification(askingContract.senderId(), askingContract.receiverId(), "elect", askingContract.senderId(), askingContract.context(), "875", "add");
        notification.refuseNotification("4");
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id=5",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal", "context"});
        assertEquals(askingContract.receiverId(), results.get(0).get(0));
        assertEquals(askingContract.senderId(), results.get(1).get(0));
        assertEquals("elect", results.get(2).get(0));
        assertEquals(askingContract.senderId(), results.get(3).get(0));
        assertEquals("Your contract request was denied by provider", results.get(4).get(0));
    }
}