package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ProposalFull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProposalManagerTest {
//TODO EFFECTUER LES TESTS !!!
    static final ProposalFull reference = new ProposalFull("2","jackie", "electricity", "100", "elec");

    @BeforeAll
    static void setUp()
    {
        //We suppose the method "addProposal" works well
        reference.setMoreInformation(20.0,20.0,20.0,true,true,null,null);
        new ProposalManager().addProposal(reference);
    }

    /**
     * We test on only one objet. The difference with multiple proposals and a single is the for-loop for the equality
     */
    @Test
    void getAllProposals()
    {

        assertEquals(reference.getProposalName(), new ProposalManager().getAllProposals(0,0).get(0).getProposalName());
    }

    @Test
    void getProposal()
    {
        assertEquals(reference.getNameProvider(), new ProposalManager().getProposal("elec", "2").getNameProvider());
    }

    @Test
    void deleteProposal()
    {
        ProposalManager proposalManager = new ProposalManager();
        proposalManager.deleteProposal(reference.getProposalName(), reference.getProviderId());
        assertEquals(proposalManager.getAllProposals(0,0).size(),0);
    }

    @Test
    void addProposal()
    {
        ProposalManager proposalManager = new ProposalManager();
        proposalManager.addProposal(reference);
        assertNotEquals(proposalManager.getAllProposals(0,0).size(),0);
    }
}