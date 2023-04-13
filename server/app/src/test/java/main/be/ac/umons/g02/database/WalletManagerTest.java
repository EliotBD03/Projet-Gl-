package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.WalletBasic;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class WalletManagerTest
{

    private final WalletBasic walletBasic = new WalletBasic("address", "name", "1", "YOU");
    private final WalletBasic walletBasicPerm = new WalletBasic("address2", "name", "2", "YOU", "R"); //Extension Claire

    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        new LogManager().saveAccount("NEVER", "GONNA", true, "YOU", "UP");

    }

    @AfterAll
    static void clean()
    {
       DB.getInstance().executeQuery("DELETE FROM wallet_contract",false);
       DB.getInstance().executeQuery("DELETE FROM wallet",false);
       DB.getInstance().executeQuery("DELETE FROM invitedTable",false); //Extension Claire
       new LogManager().deleteAccount("1");
       DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    @Order(1)
    void createWallet()
    {
        assertTrue(new WalletManager().createWallet(walletBasic));
        assertTrue(new WalletManager().createWallet(walletBasicPerm)); //Extension Claire
    }
    @Test
    @Order(2)
    void walletIsEmptyContract()
    {
        DB.getInstance().executeQuery("INSERT INTO wallet_contract(address, contract_id) VALUES('address', 1)",false);
        assertFalse(new WalletManager().walletIsEmpty("address"));
        assertTrue(new WalletManager().walletIsEmpty("addres"));
    }
    @Test
    @Order(3)
    void walletIsEmptyPermission() //Extension Claire
    {
        DB.getInstance().executeQuery("INSERT INTO invitedTable(address, invitedId, ownerId, permission) VALUES('address2', 0, 5, 'R')",false); //Extension Claire
        assertFalse(new WalletManager().walletIsEmpty("address2"));
        assertTrue(new WalletManager().walletIsEmpty("addres"));
    }
    @Test
    @Order(4)
    void getAllWallets()
    {
        ArrayList<WalletBasic> expected = ((ArrayList<WalletBasic>) new WalletManager().getAllWallets("1",0,1)[1]);
        assertNotNull(expected);
        assertEquals(expected.get(0).getAddress(), walletBasic.getAddress());
        assertEquals(expected.get(0).getName(), walletBasic.getName());
        assertEquals(expected.get(0).getClientId(), walletBasic.getClientId());
        assertEquals(expected.get(0).getOwnerName(), walletBasic.getOwnerName());
    }

    @Test
    @Order(5)
    void getAllInvitedWallets() //Extension Claire
    {
        new InvitedClientManager().addInvited("2", "address2", "1", "R");
        ArrayList<WalletBasic> expected = ((ArrayList<WalletBasic>) new WalletManager().getAllInvitedWallets("1",0,1)[1]);
        assertNotNull(expected);
        assertEquals(expected.get(0).getAddress(), walletBasicPerm.getAddress());
        assertEquals(expected.get(0).getName(), walletBasicPerm.getName());
        assertEquals(expected.get(0).getClientId(), walletBasicPerm.getClientId());
        assertEquals(expected.get(0).getOwnerName(), walletBasicPerm.getOwnerName());
        assertEquals(expected.get(0).getPermission(), walletBasicPerm.getPermission());
    }

    @Test
    @Order(6)
    void getWallet()
    {
        WalletBasic expected = new WalletManager().getWallet(walletBasic.getAddress());
        assertEquals(expected.getAddress(), walletBasic.getAddress());
        assertEquals(expected.getName(), walletBasic.getName());
        assertEquals(expected.getClientId(), walletBasic.getClientId());
        assertEquals(expected.getOwnerName(), walletBasic.getOwnerName());
    }

    @Test
    @Order(7)
    void doesTheWalletBelongToHim()
    {
        assertTrue(new WalletManager().doesTheWalletBelongToHim(walletBasic.getClientId(), walletBasic.getAddress()));
        assertFalse(new WalletManager().doesTheWalletBelongToHim(walletBasic.getClientId(),"adresse factice"));
    }

    @Test
    @Order(8)
    void deleteWallet()
    {
        WalletManager walletManager = new WalletManager();
        System.out.println("ici toto");
        assertTrue(walletManager.deleteWallet("nhjgkfldms"));
        assertFalse(walletManager.deleteWallet(walletBasic.getAddress()));
        assertFalse(walletManager.deleteWallet(walletBasicPerm.getAddress())); //Extension Claire
    }
}