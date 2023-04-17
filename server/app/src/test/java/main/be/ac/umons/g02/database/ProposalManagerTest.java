package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProposalManagerTest
{
    static final ProposalFull reference = new ProposalFull("1","jackie", "electricity", "100", "elec");

    @BeforeAll
    static void setUp() throws Exception
    {
        DBTest.setUp();
        new LogManager().saveAccount("jackie@gmail.com", "johnie", false, "jackie", "english");
        reference.setMoreInformation(20.0,20.0,false,"20:15:00","06:15:00", 6);
    }

    @AfterAll
    static void clean()
    {
        new Query("DELETE FROM proposal");
        new LogManager().deleteAccount("1");
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    @Order(1)
    void addProposal()
    {
        ProposalManager proposalManager = new ProposalManager();
        proposalManager.addProposal(reference);
        assertNotEquals(((ArrayList<ProposalBasic>)proposalManager.getAllProposals(null, null,0,1 )[1]).size(),0);
    }

    /**
     * We test on only one objet. The difference with multiple proposals and a single is the for-loop for the equality
     */
    @Test
    @Order(2)
    void getAllProposals()
    {
        ProposalBasic toBeTested = ((ArrayList<ProposalBasic>)new ProposalManager().getAllProposals("electricity", null,0,1)[1]).get(0);
        assertEquals(toBeTested.getLocation(), reference.getLocation());
        assertEquals(toBeTested.getProposalName(), reference.getProposalName());
        assertEquals(toBeTested.getTypeOfEnergy(), reference.getTypeOfEnergy());
        assertEquals(toBeTested.getNameProvider(), reference.getNameProvider());
        assertEquals(toBeTested.getProviderId(), reference.getProviderId());

        Object[] expectedEmptyList = new ProposalManager().getAllProposals("gas", "010", 0, 1);
        assertEquals(expectedEmptyList[1], Table.EMPTY_TABLE);
    }

    @Test
    @Order(3)
    void getAllProposalsFromAProvider()
    {
        ProposalBasic toBeTested = ((ArrayList<ProposalBasic>)new ProposalManager().getAllProposals("1",0,1)[1]).get(0);
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
        assertEquals(toBeTested.getVariableDayPrice(), reference.getVariableDayPrice());
        assertEquals(toBeTested.getVariableNightPrice(), reference.getVariableNightPrice());
        assertEquals(toBeTested.isFixedRate(), reference.isFixedRate());
        assertEquals(toBeTested.getStartOfPeakHours(), reference.getStartOfPeakHours());
        assertEquals(toBeTested.getEndOfPeakHours(), reference.getEndOfPeakHours());
    }

    @Test
    @Order(5)
    void changeVariablePrice()
    {
        ProposalFull proposalFull = new ProposalFull("1","jackie","gas","101","gasssss");
        proposalFull.setMoreInformation(20,21,true,"23:01:54","02:35:01",7);
        new ProposalManager().addProposal(proposalFull);
        assertTrue(new ProposalManager().changeVariablePrice("gasssss", "1", 54, 56));
        assertFalse(new ProposalManager().changeVariablePrice("elec", "1", 56, 57));
    }

    @Test
    @Order(6)
    void deleteProposal()
    {
        ProposalManager proposalManager = new ProposalManager();
        proposalManager.deleteProposal(reference.getProposalName(), reference.getProviderId());
        assertEquals(((ArrayList<ProposalBasic>) proposalManager.getAllProposals("1",0,0)[1]).size(),0);
    }
}