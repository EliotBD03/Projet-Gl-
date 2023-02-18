package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;

import java.util.ArrayList;
import java.util.Calendar;

public class ProposalManager
{

    private boolean doesTheProposalExist(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM proposal WHERE proposal_name='"+proposalName
                +"' AND provider_id="+providerId+") AS c",true);
        return Integer.parseInt(DB.getInstance().getResults(new String[] {"c"}).get(0).get(0)) == 1;
    }

    public ArrayList<ProposalBasic> getAllProposals()
    {
        return null; //TODO
    }

    public ArrayList<ProposalBasic> getAllProposals(String providerId)
    {
        return null; //TODO
    }

    public ProposalFull getProposal(String proposalId)
    {
        return null; //TODO
    }

    public void addProposal(ProposalFull proposal) throws Exception
    {
        if(doesTheProposalExist(proposal.getProposalName(), proposal.getProviderId()))
            throw new Exception("The proposal already exists");

        String startOffPeakHours = proposal.getStartOfPeakHours().get(Calendar.HOUR) + ":" + proposal.getStartOfPeakHours().get(Calendar.MINUTE) + proposal.getStartOfPeakHours().get(Calendar.SECOND);
        String endOffPeakHours = proposal.getEndOfPeakHours().get(Calendar.HOUR) + ":" + proposal.getEndOfPeakHours().get(Calendar.MINUTE) + proposal.getEndOfPeakHours().get(Calendar.SECOND);
        DB.getInstance().executeQuery("INSERT INTO consumption(proposal_name, provider_id, water"+
                ",gas,electricity,fixed_rate,peak_hours,offpeak_hours,start_peak_hours,end_peak_hours,price,location"
                + "VALUES('"+proposal.getProposalName() + "',"
                + proposal.getProviderId()+ "',"
                + ((proposal.getTypeOfEnergy().ordinal() == 0) ? 1 : 0) + ","
                + ((proposal.getTypeOfEnergy().ordinal() == 1) ? 1 : 0) + ","
                + ((proposal.getTypeOfEnergy().ordinal() == 2) ? 1 : 0) + ","
                + proposal.isFixedRate() + ","
                + proposal.getVariableNightPrice() + ","
                + proposal.getVariableDayPrice() + ","
                + startOffPeakHours + ","
                + endOffPeakHours + ","
                + proposal.getBasicPrice() + ","
                + proposal.getLocation() + ")",false);
        //TODO location -> bit
    }

    public void changeProposal(ProposalFull proposalFull) throws Exception
    {
        deleteProposal(proposalFull.getProposalName(), proposalFull.getProviderId());
        addProposal(proposalFull);
    }

    public void deleteProposal(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("DELETE FROM proposal WHERE proposal_name='"+proposalName+"' AND provider_id="+providerId, false);
    }

}
