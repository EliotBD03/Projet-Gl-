package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Location;
import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.TypeEnergy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProposalManagerTest {
//TODO EFFECTUER LES TESTS !!!
    static final ProposalFull reference = new ProposalFull("2","jackie", TypeEnergy.ELECTRICITY, new Location[] {Location.BRUSSELS}, "elec");

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

        assertEquals(reference.getProposalName(), new ProposalManager().getAllProposals().get(0).getProposalName());
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
        assertEquals(proposalManager.getAllProposals().size(),0);
    }

    @Test
    void addProposal()
    {
        ProposalManager proposalManager = new ProposalManager();
        proposalManager.addProposal(reference);
        assertNotEquals(proposalManager.getAllProposals().size(),0);
    }
}