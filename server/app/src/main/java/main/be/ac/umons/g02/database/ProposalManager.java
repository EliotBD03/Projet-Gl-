package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;

import java.util.ArrayList;


public class ProposalManager
{

    private final String[] typeOfEnergy = new String[] {"water", "gas", "electricity"};

    public boolean doesTheProposalExist(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM proposal WHERE proposal_name='"+proposalName
                +"' AND provider_id="+providerId+") AS c",true);
        return Integer.parseInt(DB.getInstance().getResults(new String[] {"c"}).get(0).get(0)) == 1;
    }

    public ArrayList<ProposalBasic> getAllProposals(String providerId, int base, int limit)
    {
        String query = "SELECT * FROM proposal WHERE provider_id ="+providerId+" LIMIT "+base+","+(base+limit);
        DB.getInstance().executeQuery(query, true);
        ArrayList<ArrayList<String>> results = new ArrayList<>(DB.getInstance().getResults(new String[] {"proposal_name","provider_id",
                "water", "gas", "electricity", "location"}));

        String proposalName;
        String nameProvider;
        String typeEnergy = null;
        String location;

        ArrayList<ProposalBasic> proposalBasics = new ArrayList<>();
        for(int i = 0; i < results.get(0).size(); i ++)
        {
            proposalName = results.get(0).get(i);

            for(int j = 0 ; j < typeOfEnergy.length; j++)
                if(results.get(2+j).get(i).equals("1"))
                    typeEnergy = typeOfEnergy[j];

            location = results.get(results.size() - 1).get(i);

            DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+providerId,true);
            nameProvider = DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);

            proposalBasics.add(new ProposalBasic(proposalName, providerId, nameProvider, typeEnergy, location));
        }
        return proposalBasics;
    }

    public ArrayList<ProposalBasic> getAllProposals(String energyCategory, String regionCategory, int base, int limit) //TODO je suis amnésique je pense
    {
        String query = "SELECT * FROM proposal LIMIT "+base+", "+(base+limit);
        if(energyCategory != null && regionCategory != null)
            query = "SELECT * FROM proposal WHERE "+energyCategory+"=1 AND location="+regionCategory + " LIMIT "+base+", "+(base+limit);
        else if(energyCategory != null)
            query = "SELECT * FROM proposal WHERE "+energyCategory+"=1 LIMIT "+base+", "+(base+limit);
        else if(regionCategory != null)
            query = "SELECT * FROM proposal WHERE location="+regionCategory + " LIMIT "+base+", "+(base+limit);

        DB.getInstance().executeQuery("SELECT * FROM proposal LIMIT "+base+", "+(base+limit), true);
        ArrayList<ArrayList<String>> results = new ArrayList<>(DB.getInstance().getResults(new String[] {"proposal_name","provider_id",
                "water", "gas", "electricity", "location"}));

        String proposalName;
        String providerId;
        String nameProvider;
        String typeEnergy = null;
        String location;

        ArrayList<ProposalBasic> proposalBasics = new ArrayList<>();
        for(int i = 0; i < results.get(0).size(); i ++)
        {
            proposalName = results.get(0).get(i);
            providerId = results.get(1).get(i);


            for(int j = 0 ; j < typeOfEnergy.length; j++)
                if(results.get(2+j).get(i).equals("1"))
                    typeEnergy = typeOfEnergy[j];

            location = results.get(results.size() - 1).get(i);

            DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+providerId,true);
            nameProvider = DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);

            proposalBasics.add(new ProposalBasic(proposalName, providerId, nameProvider, typeEnergy, location));
        }
        return proposalBasics;
    }

    public ProposalFull getProposal(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("SELECT * FROM proposal WHERE proposal_name='"+proposalName+
                "' AND provider_id="+providerId, true);

        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[]{"proposal_name","provider_id","water"
        ,"gas","electricity","fixed_rate","peak_hours","offpeak_hours","start_peak_hours","end_peak_hours","price","location", "duration"});

        ProposalFull proposalFull;
        String typeEnergy = null;

        for(int i = 0; i < typeOfEnergy.length; i++)
            if(results.get(i + 2).get(0).equals("1"))
                typeEnergy = typeOfEnergy[i];

        boolean fixeRate = results.get(5).get(0).equals("1");
        double peakHours = Double.parseDouble(results.get(6).get(0));
        double offPeakHours = Double.parseDouble(results.get(7).get(0));
        String startPeakHours = results.get(8).get(0);
        String endPeakHours = results.get(9).get(0);
        double basicPrice = Double.parseDouble(results.get(10).get(0));
        String location = results.get(11).get(0);
        String duration = results.get(12).get(0);

        String nameProvider;
        DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+providerId,true);
        nameProvider = DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);

        proposalFull = new ProposalFull(providerId, nameProvider, typeEnergy, location, proposalName);
        proposalFull.setMoreInformation(basicPrice, peakHours, offPeakHours, fixeRate, startPeakHours != null, startPeakHours, endPeakHours, Integer.parseInt(duration));
        return proposalFull;
    }

    public boolean addProposal(ProposalFull proposal)
    {
        boolean value = false;
        if(doesTheProposalExist(proposal.getProposalName(), proposal.getProviderId()))
        {
            deleteProposal(proposal.getProposalName(), proposal.getProviderId());
            value = true;
        }

        String query = "INSERT INTO proposal(proposal_name, provider_id, water"+
                ",gas,electricity,fixed_rate,peak_hours,offpeak_hours,start_peak_hours,end_peak_hours,price,location, duration)"
                + " VALUES('"+proposal.getProposalName() + "',"
                + proposal.getProviderId()+ ","
                + ((proposal.getTypeOfEnergy().equals("water")) ? 1 : 0) + ","
                + ((proposal.getTypeOfEnergy().equals("gas")) ? 1 : 0) + ","
                + ((proposal.getTypeOfEnergy().equals("electricity")) ? 1 : 0) + ","
                + (proposal.isFixedRate() ? 1 : 0) + ","
                + proposal.getVariableNightPrice() + ","
                + proposal.getVariableDayPrice() + ","
                + (proposal.getStartOfPeakHours() == null ? "DEFAULT" : "'"+proposal.getStartOfPeakHours()+"'") + ","
                + (proposal.getEndOfPeakHours() == null ? "DEFAULT" : "'"+proposal.getEndOfPeakHours()+"'") + ","
                + proposal.getBasicPrice() + ","
                + proposal.getLocation() + ","
                + proposal.getDuration() +");";

        DB.getInstance().executeQuery(query,false);

        return value;
    }

    public void deleteProposal(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("DELETE FROM proposal WHERE proposal_name='"+proposalName+"' AND provider_id="+providerId, false);
    }

}
