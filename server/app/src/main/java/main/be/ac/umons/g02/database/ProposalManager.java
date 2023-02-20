package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Location;
import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.TypeEnergy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProposalManager
{

    private boolean doesTheProposalExist(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM proposal WHERE proposal_name='"+proposalName
                +"' AND provider_id="+providerId+") AS c",true);
        return Integer.parseInt(DB.getInstance().getResults(new String[] {"c"}).get(0).get(0)) == 1;
    }

    private GregorianCalendar getFormatTime(String time)
    {
        if(time == null)
            return null;
        String[] values = time.split(":");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(values[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(values[1]));
        calendar.set(Calendar.SECOND, Integer.parseInt(values[2]));
        return calendar;
    }

    public ArrayList<ProposalBasic> getAllProposals()
    {
        DB.getInstance().executeQuery("SELECT * FROM proposal", true);
        ArrayList<ArrayList<String>> results = new ArrayList<>(DB.getInstance().getResults(new String[] {"proposal_name","provider_id",
                "water", "gas", "electricity", "location"}));

        String proposalName;
        String providerId;
        String nameProvider;
        TypeEnergy typeEnergy = null;
        Location[] location = new Location[3];

        ArrayList<ProposalBasic> proposalBasics = new ArrayList<>();
        for(int i = 0; i < results.get(0).size(); i ++)
        {
            proposalName = results.get(0).get(i);
            providerId = results.get(1).get(i);

            for(int j = 0 ; j < TypeEnergy.values().length ; j++)
                if(results.get(2+j).get(i).equals("1"))
                    typeEnergy = TypeEnergy.values()[j];

            for(int j = 0; j < location.length; j++)
                location[j] = (results.get(2 + TypeEnergy.values().length - 1).get(i).substring(j).equals("1")) ? Location.values()[j] : null;

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
        ,"gas","electricity","fixed_rate","peak_hours","offpeak_hours","start_peak_hours","end_peak_hours","price","location"});

        ProposalFull proposalFull;
        TypeEnergy typeEnergy = null;

        for(int i = 0; i < TypeEnergy.values().length; i++)
            if(results.get(i + 2).get(0).equals("1"))
                typeEnergy = TypeEnergy.values()[i];

        boolean fixeRate = results.get(5).get(0).equals("1");
        double peakHours = Double.parseDouble(results.get(6).get(0));
        double offPeakHours = Double.parseDouble(results.get(7).get(0));
        GregorianCalendar startPeakHours = getFormatTime(results.get(8).get(0));
        GregorianCalendar endPeakHours = getFormatTime(results.get(9).get(0));
        double basicPrice = Double.parseDouble(results.get(10).get(0));
        Location[] location = new Location[3];

        for(int j = 0; j < location.length; j++)
            location[j] = (results.get(2 + TypeEnergy.values().length - 1).get(0).substring(j).equals("1")) ? Location.values()[j] : null;


        String nameProvider;
        DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+providerId,true);
        nameProvider = DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);

        proposalFull = new ProposalFull(providerId, nameProvider, typeEnergy, location, proposalName);
        proposalFull.setMoreInformation(basicPrice, peakHours, offPeakHours, fixeRate, peakHours == offPeakHours, startPeakHours, endPeakHours);
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


        String startOffPeakHours = "NULL";
        String endOffPeakHours = "NULL";
        if(!proposal.getIsSingleHour())
        {
            startOffPeakHours = proposal.getStartOfPeakHours().get(Calendar.HOUR) + ":" + proposal.getStartOfPeakHours().get(Calendar.MINUTE) + proposal.getStartOfPeakHours().get(Calendar.SECOND);
            endOffPeakHours = proposal.getEndOfPeakHours().get(Calendar.HOUR) + ":" + proposal.getEndOfPeakHours().get(Calendar.MINUTE) + proposal.getEndOfPeakHours().get(Calendar.SECOND);
        }

        int location = 0;
        for(int i = 0; i < proposal.getLocation().length; i++)
                location += (proposal.getLocation()[i] != null ? 1 : 0) << proposal.getLocation()[i].ordinal(); //TODO MAKE TEST 'CAUSE DONT KNOW IF IT WORKS

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
                + location + ")",false);

        return value;
    }

    public void deleteProposal(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("DELETE FROM proposal WHERE proposal_name='"+proposalName+"' AND provider_id="+providerId, false);
    }

}
