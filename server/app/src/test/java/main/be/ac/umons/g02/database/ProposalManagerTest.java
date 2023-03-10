package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProposalManagerTest
{

//TODO EFFECTUER LES TESTS !!!
    static final ProposalFull reference = new ProposalFull("1","jackie", "electricity", "100", "elec");

    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        new LogManager().saveAccount("jackie@gmail.com", "johnie", false, "jackie", "english");
        //We suppose the method "addProposal" works well
        reference.setMoreInformation(20.0,20.0,20.0,true,false,"20:15:00","06:15:00", 6);
    }

    @AfterAll
    static void clean()
    {
        new ProposalManager().deleteProposal("elec", "1");
        new LogManager().deleteAccount("1");
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    @Order(1)
    void addProposal()
    {
        ProposalManager proposalManager = new ProposalManager();
        proposalManager.addProposal(reference);
        assertNotEquals(proposalManager.getAllProposals(0,1).size(),0);
    }

    /**
     * We test on only one objet. The difference with multiple proposals and a single is the for-loop for the equality
     */
    @Test
    @Order(2)
    void getAllProposals()
    {
        ProposalBasic toBeTested = new ProposalManager().getAllProposals(0,1).get(0);
        assertEquals(toBeTested.getLocation(), reference.getLocation());
        assertEquals(toBeTested.getProposalName(), reference.getProposalName());
        assertEquals(toBeTested.getTypeOfEnergy(), reference.getTypeOfEnergy());
        assertEquals(toBeTested.getNameProvider(), reference.getNameProvider());
        assertEquals(toBeTested.getProviderId(), reference.getProviderId());
    }

    @Test
    @Order(3)
    void getAllProposalsFromAProvider()
    {
        ProposalBasic toBeTested = new ProposalManager().getAllProposals("1",0,1).get(0);
        assertEquals(toBeTested.getLocation(), reference.getLocation());
        assertEquals(toBeTested.getProposalName(), reference.getProposalName());
        assertEquals(toBeTested.getTypeOfEnergy(), reference.getTypeOfEnergy());
        assertEquals(toBeTested.getNameProvider(), reference.getNameProvider());
        assertEquals(toBeTested.getProviderId(), reference.getProviderId());
    }

    @Test
    @Order(4)
    void getProposal()
    {
        ProposalFull toBeTested = new ProposalManager().getProposal("elec", "1");
        assertEquals(toBeTested.getBasicPrice(), reference.getBasicPrice());
        assertEquals(toBeTested.getVariableDayPrice(), reference.getVariableDayPrice());
        assertEquals(toBeTested.getVariableNightPrice(), reference.getVariableNightPrice());
        assertEquals(toBeTested.isFixedRate(), reference.isFixedRate());
        assertEquals(toBeTested.getIsSingleHour(), reference.getIsSingleHour());
        assertEquals(toBeTested.getStartOfPeakHours(), reference.getStartOfPeakHours());
        assertEquals(toBeTested.getEndOfPeakHours(), reference.getEndOfPeakHours());
    }

    @Test
    @Order(5)
    void deleteProposal()
    {
        ProposalManager proposalManager = new ProposalManager();
        proposalManager.deleteProposal(reference.getProposalName(), reference.getProviderId());
        assertEquals(proposalManager.getAllProposals(0,0).size(),0);
    }
}