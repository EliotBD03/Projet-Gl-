package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ProposalFull;
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
        ProposalFull proposalFull = new ProposalFull("2", "provider", "electricity","100","elec");
        new ProposalManager().addProposal(proposalFull);


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