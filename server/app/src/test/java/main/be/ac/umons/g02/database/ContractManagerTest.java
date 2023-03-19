package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.ContractFull;
import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.WalletBasic;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContractManagerTest
{
    private static ProposalFull proposalFull = new ProposalFull("2", "provider", "electricity","100","elec");
    private static ContractFull expected = new ContractFull("1","785", "2", "1", "provider", "client");
    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        proposalFull.setMoreInformation(20.0,20.0,20.0,true,true,null,null,6);
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
    @Order(1)
    void createContract()
    {
        assertTrue(new ContractManager().createContract("elec", "785", "2", "address", "1"));
    }

    @Test
    @Order(2)
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
    @Order(3)
    void getAllContracts()
    {
        ContractBasic toBeTested = ((ArrayList<ContractBasic>)new ContractManager().getAllContracts("1", 0,1)[1]).get(0);
        assertEquals(toBeTested.getContractId(), expected.getContractId());
        assertEquals(toBeTested.getProviderId(), expected.getProviderId());
        assertEquals(toBeTested.getEan(), expected.getEan());
        assertEquals(toBeTested.getClientId(), expected.getClientId());
        assertEquals(toBeTested.getClientName(), expected.getClientName());
        assertEquals(toBeTested.getProviderName(), expected.getProviderName());
    }

    @Test
    @Order(4)
    void getCommonContracts()
    {
        ContractBasic toBeTested = ((ArrayList<ContractBasic>)new ContractManager().getCommonContracts("1", "2", 0, 1)[1]).get(0);
        assertEquals(toBeTested.getContractId(), expected.getContractId());
        assertEquals(toBeTested.getProviderId(), expected.getProviderId());
        assertEquals(toBeTested.getEan(), expected.getEan());
        assertEquals(toBeTested.getClientId(), expected.getClientId());
        assertEquals(toBeTested.getClientName(), expected.getClientName());
        assertEquals(toBeTested.getProviderName(), expected.getProviderName());
    }

    @Test
    @Order(5)
    void getAllClientsOfContract()
    {
        ArrayList<String> arrayList = new ArrayList<>()
        {{
            add("1");
        }};
        assertEquals(arrayList, new ContractManager().getAllClientsOfContract(proposalFull.getProposalName(), proposalFull.getProviderId()));
    }

    @Test
    @Order(6)
    void getTypeOfEnergy()
    {
        WalletManager.energyType expected = WalletManager.energyType.ELECTRICITY;
        assertEquals(expected, new ContractManager().getTypeOfEnergy(ContractManagerTest.expected.getAddress()));
    }

    @Test
    @Order(7)
    void deleteContract()
    {
        new ContractManager().deleteContract("1");
        DB.getInstance().executeQuery("SELECT * from provider_contract e, wallet_contract, counter, contract WHERE e.contract_id=1",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"*"});
        assertEquals(results.get(0).size(), 0);
    }

}