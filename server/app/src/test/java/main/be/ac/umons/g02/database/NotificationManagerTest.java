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
    private static Notification createdContract = new Notification("1", "1", "2", "1", "contract created", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
    private static Notification askingContract = new Notification("2", "1", "2", "2", "contract request", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
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
        new NotificationManager().createNotification(createdContract.getSenderId(), createdContract.getReceiverId(),"elec","2", createdContract.getContext(), "785", "address");
        DB.getInstance().executeQuery("SELECT * FROM notification", true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name","provider_id_proposal", "context", "linked_ean", "linked_address", "creation_date"});
        assertEquals(createdContract.getCreationDate(), results.get(7).get(0)); //be careful, can be false for the time between the creation and the reception of the notif
        assertEquals(createdContract.getSenderId(), results.get(0).get(0));
        assertEquals(createdContract.getReceiverId(), results.get(1).get(0));
        assertEquals("elec", results.get(2).get(0));
        assertEquals("2", results.get(3).get(0));
        assertEquals(createdContract.getContext(), results.get(4).get(0));
        assertEquals("785", results.get(5).get(0));
        assertEquals("address", results.get(6).get(0));
    }

    @Test
    @Order(2)
    void getAllNotifications()
    {
        Notification toBeTested = ((ArrayList<Notification>) new NotificationManager().getAllNotifications("2", 0, 1)[1]).get(0);
        assertEquals(createdContract.getCreationDate(), toBeTested.getCreationDate());
        assertEquals(createdContract.getId_notification(), toBeTested.getId_notification());
        assertEquals(createdContract.getContext(), toBeTested.getContext());
        assertEquals(createdContract.getReceiverId(), createdContract.getReceiverId());
        assertEquals(createdContract.getSenderId(), createdContract.getSenderId());
    }

    @Test
    @Order(3)
    void acceptNotification() throws Exception
    {
        NotificationManager notification = new NotificationManager();
        notification.createNotification(askingContract.getSenderId(), askingContract.getReceiverId(), "elect", askingContract.getSenderId(), askingContract.getContext(), "875", "add");

        LogManager logManager = new LogManager();
        logManager.saveAccount("clientmail", "password", true, "client", "english");
        logManager.saveAccount("providermail", "password", false, "provider", "english");

        new ProposalManager().addProposal(new ProposalFull("2", "provider", "electricity", "100", "elect"));
        new WalletManager().createWallet(new WalletBasic("add", "wallet", "1", "client"));

        notification.acceptNotification("2", "785", "add");
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id=3",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal", "context"});
        assertEquals(askingContract.getReceiverId(), results.get(0).get(0));
        assertEquals(askingContract.getSenderId(), results.get(1).get(0));
        assertEquals("elect", results.get(2).get(0));
        assertEquals(askingContract.getSenderId(), results.get(3).get(0));
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
        notification.createNotification(askingContract.getSenderId(), askingContract.getReceiverId(), "elect", askingContract.getSenderId(), askingContract.getContext(), "875", "add");
        notification.refuseNotification("4");
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id=5",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal", "context"});
        assertEquals(askingContract.getReceiverId(), results.get(0).get(0));
        assertEquals(askingContract.getSenderId(), results.get(1).get(0));
        assertEquals("elect", results.get(2).get(0));
        assertEquals(askingContract.getSenderId(), results.get(3).get(0));
        assertEquals("Your contract request was denied by provider", results.get(4).get(0));
    }
}