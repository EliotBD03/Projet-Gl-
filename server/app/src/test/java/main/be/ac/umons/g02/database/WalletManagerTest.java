package main.be.ac.umons.g02.database;

import com.mysql.cj.log.Log;
import main.be.ac.umons.g02.data_object.WalletBasic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletManagerTest
{

    private final WalletBasic walletBasic = new WalletBasic("address", "name", "1");

    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        new LogManager().saveAccount("NEVER", "GONNA", true, "YOU", "UP");

    }

    @AfterAll
    static void clean()
    {

    }

    @Test
    void walletIsEmpty()
    {
        assertTrue(new WalletManager().walletIsEmpty("address"));
        assertFalse(new WalletManager().walletIsEmpty("addres"));
    }

    @Test
    void createWallet()
    {
        assertTrue(new WalletManager().createWallet(walletBasic));
    }
    @Test
    void getAllWallets()
    {
        WalletBasic expected = new WalletManager().getAllWallets("1",1 ,1).get(0);
        assertEquals(expected.getAddress(), walletBasic.getAddress());
        assertEquals(expected.getName(), walletBasic.getName());
        assertEquals(expected.getClientId(), walletBasic.getClientId());
    }

    @Test
    void getWallet()
    {
        WalletBasic expected = new WalletManager().getWallet(walletBasic.getAddress());
        assertEquals(expected.getAddress(), walletBasic.getAddress());
        assertEquals(expected.getName(), walletBasic.getName());
        assertEquals(expected.getClientId(), walletBasic.getClientId());
    }

    @Test
    void deleteWallet()
    {

    }
}