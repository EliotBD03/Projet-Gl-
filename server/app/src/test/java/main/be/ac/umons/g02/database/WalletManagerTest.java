package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.WalletBasic;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class WalletManagerTest
{

    private final WalletBasic walletBasic = new WalletBasic("address", "name", "1", "YOU");

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
        new LogManager().deleteAccount("1");
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    @Order(1)
    void createWallet()
    {
        assertTrue(new WalletManager().createWallet(walletBasic));
    }
    @Test
    @Order(2)
    void walletIsEmpty()
    {
        assertFalse(new WalletManager().walletIsEmpty("address"));
        assertTrue(new WalletManager().walletIsEmpty("addres"));
    }

    @Test
    @Order(3)
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
    @Order(4)
    void getWallet()
    {
        WalletBasic expected = new WalletManager().getWallet(walletBasic.getAddress());
        assertEquals(expected.getAddress(), walletBasic.getAddress());
        assertEquals(expected.getName(), walletBasic.getName());
        assertEquals(expected.getClientId(), walletBasic.getClientId());
        assertEquals(expected.getOwnerName(), walletBasic.getOwnerName());
    }

    @Test
    @Order(5)
    void deleteWallet()
    {
        WalletManager walletManager = new WalletManager();
        assertFalse(walletManager.deleteWallet("nhjgkfldms"));
        assertTrue(walletManager.deleteWallet(walletBasic.getAddress()));
    }
}