package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.WalletBasic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContractManagerTest
{
    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        LogManager logManager = new LogManager();
        logManager.saveAccount("client@gmail.com", "clientPassword", true, "client", "english");
        logManager.saveAccount("provider@gmail.com", "providerPassword", false, "provider", "english");
        new WalletManager().createWallet(new WalletBasic("address", "name", "1"));
        ProposalFull proposalFull = new ProposalFull("2", "provider", "electricity","100","elec");
        new ProposalManager().addProposal(proposalFull);
    }

    @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("DELETE  FROM counter",false);
        DB.getInstance().executeQuery("DELETE  FROM contract",false);
        DB.getInstance().executeQuery("DELETE  FROM provider_contract", false);
        DB.getInstance().executeQuery("DELETE  FROM wallet_contract", false);
        DB.getInstance().executeQuery("DELETE  FROM wallet", false);
        LogManager logManager = new LogManager();
        logManager.deleteAccount("1");
        logManager.deleteAccount("2");
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    void createContract()
    {
        assertTrue(new ContractManager().createContract("elec", "785", "2", "address", "1"));
    }

    @Test
    void getContract()
    {

    }

    @Test
    void getAllContracts()
    {
    }

    @Test
    void deleteContract()
    {
    }

    @Test
    void getCommonContracts()
    {

    }

    @Test
    void getAllClientsOfContract() {
    }

    @Test
    void getTypeOfEnergy()
    {

    }

}