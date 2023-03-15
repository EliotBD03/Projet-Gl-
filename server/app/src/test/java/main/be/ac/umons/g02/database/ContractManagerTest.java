package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.ContractFull;
import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.WalletBasic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContractManagerTest
{
    private static ProposalFull proposalFull = new ProposalFull("2", "provider", "electricity","100","elec");
    private static ContractFull expected = new ContractFull("1","785", "2", "1", "provider", "client");
    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        expected.setMoreInformation(proposalFull, null, null,  "address");
        LogManager logManager = new LogManager();
        logManager.saveAccount("client@gmail.com", "clientPassword", true, "client", "english");
        logManager.saveAccount("provider@gmail.com", "providerPassword", false, "provider", "english");
        new WalletManager().createWallet(new WalletBasic("address", "name", "1"));
        new ProposalManager().addProposal(proposalFull);
    }

    @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("DELETE FROM contract",false);
        DB.getInstance().executeQuery("DELETE FROM counter",false);
        System.out.println("je passe là");
        new ProposalManager().deleteProposal("elec", "2");
        DB.getInstance().executeQuery("DELETE FROM provider_contract", false);
        DB.getInstance().executeQuery("DELETE FROM wallet_contract", false);
        new WalletManager().deleteWallet("address");
        LogManager logManager = new LogManager();
        logManager.deleteAccount("1");
        logManager.deleteAccount("2");
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE contract AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE wallet_contract AUTO_INCREMENT = 1", false);
        DB.getInstance().executeQuery("ALTER TABLE provider_contract AUTO_INCREMENT = 1", false);
    }


    @Test
    void createContract()
    {
        assertTrue(new ContractManager().createContract("elec", "785", "2", "address", "1"));
    }

    @Test
    void getContract()
    {
        ContractFull toBeTested = new ContractManager().getContract("1");
        assertEquals(expected.getBasicPrice(), toBeTested.getBasicPrice());
        assertEquals(expected.getProposalName(), toBeTested.getProposalName());
        assertEquals(expected.getVariableDayPrice(), toBeTested.getVariableDayPrice());
        assertEquals(expected.getEndOfPeakHours(), toBeTested.getEndOfPeakHours());
        assertEquals(expected.getAddress(), toBeTested.getAddress());
        assertEquals(expected.getIsFixedDate(), toBeTested.getIsFixedDate());
        assertEquals(expected.getIsSingleHourCounter(), toBeTested.getIsSingleHourCounter());
    }

    @Test
    void getAllContracts()
    {
        ContractBasic toBeTested = new ContractManager().getAllContracts("1", 0,1).get(0);
        assertEquals(toBeTested.getContractId(), expected.getContractId());
        assertEquals(toBeTested.getProviderId(), expected.getProviderId());
        assertEquals(toBeTested.getEan(), expected.getEan());
        assertEquals(toBeTested.getClientId(), expected.getClientId());
        assertEquals(toBeTested.getClientName(), expected.getClientName());
        assertEquals(toBeTested.getProviderName(), expected.getProviderName());
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