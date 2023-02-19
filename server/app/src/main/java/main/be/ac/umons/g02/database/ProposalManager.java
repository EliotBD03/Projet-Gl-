package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Location;
import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.TypeEnergy;

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

    public ArrayList<ProposalBasic> getAllProposals(String providerId)
    {
        String query;
        if(providerId != null)
            query = "SELECT * FROM proposal WHERE provider_id="+providerId;
        else
            query = "SELECT * FROM proposal";

        DB.getInstance().executeQuery(query, true);
        ArrayList<ArrayList<String>> results = new ArrayList<>(DB.getInstance().getResults(new String[] {"proposal_name","provider_id",
                "water", "gas", "electricity", "location"}));

        String proposalName;
        String nameProvider;
        TypeEnergy[] typeEnergy = new TypeEnergy[3];
        Location[] location = new Location[3];

        ArrayList<ProposalBasic> proposalBasics = new ArrayList<>();
        for(int i = 0; i < results.get(0).size(); i ++)
        {
            proposalName = results.get(0).get(i);
            providerId = results.get(1).get(i);
            DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+providerId,true);
            nameProvider = DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);

            for(int j = 0 ; j < typeEnergy.length ; j++)
                typeEnergy[j] = (results.get(2 + j).get(i).equals("1")) ? TypeEnergy.values()[j] : null;

            for(int j = 0; j < location.length; j++)
                location[j] = (results.get(2 + typeEnergy.length - 1).get(i).substring(j).equals("1")) ? Location.values()[j] : null;

            proposalBasics.add(new ProposalBasic(proposalName, providerId, nameProvider, typeEnergy, location));
        }
        return proposalBasics;
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

        int location = 0;
        for(int i = 0; i < proposal.getLocation().length; i++)
                location += (proposal.getLocation()[i] != null ? 1 : 0) << proposal.getLocation()[i].ordinal(); //TODO MAKE TEST 'CAUSE DONT KNOW IF IT WORKS

        DB.getInstance().executeQuery("INSERT INTO consumption(proposal_name, provider_id, water"+
                ",gas,electricity,fixed_rate,peak_hours,offpeak_hours,start_peak_hours,end_peak_hours,price,location"
                + "VALUES('"+proposal.getProposalName() + "',"
                + proposal.getProviderId()+ "',"
                + ((proposal.getTypeOfEnergy()[0] != null) ? 1 : 0) + ","
                + ((proposal.getTypeOfEnergy()[1] != null) ? 1 : 0) + ","
                + ((proposal.getTypeOfEnergy()[2] != null) ? 1 : 0) + ","
                + proposal.isFixedRate() + ","
                + proposal.getVariableNightPrice() + ","
                + proposal.getVariableDayPrice() + ","
                + startOffPeakHours + ","
                + endOffPeakHours + ","
                + proposal.getBasicPrice() + ","
                + location + ")",false);
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
