package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;

import java.util.ArrayList;


public class ProposalManager
{

    private final String[] typeOfEnergy = new String[] {"water", "gas", "electricity"};

    /**
     * Vérifie si la proposition existe.
     *
     * @param proposalName le nom de la propo
     * @param providerId
     * @return
     */
    public boolean doesTheProposalExist(String proposalName, String providerId)
    {
        return new Query("SELECT EXISTS(SELECT * FROM proposal WHERE proposal_name='"+proposalName
                +"' AND provider_id="+providerId+") AS c").executeAndGetResult("c").getIntElem(0,0) >= 1;
    }

    /**
     * Renvoie toutes les propositions d'un certain fournisseur dans un intervalle : [base,base+limit]
     * en plus du nombre de propositions que le fournisseur possède.
     *
     * @param providerId l'identifiant du fournisseur
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return un tableau contenant en premier indice le nombre d'éléments total et une ArrayList contenant des ProposalBasics.
     */
    public Object[] getAllProposals(String providerId, int base, int limit)
    {
        String query = "SELECT * FROM proposal WHERE provider_id ="+providerId+" LIMIT "+base+","+limit;
        DB.getInstance().executeQuery(query, true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("proposal_name","provider_id",
                "water", "gas", "electricity", "location").getTable();

        if(table == null)
            return null;

        table = new ArrayList<>(table);

        String proposalName;
        String nameProvider;
        String typeEnergy = null;
        String location;

        ArrayList<ProposalBasic> proposalBasics = new ArrayList<>();
        for(int i = 0; i < table.size(); i ++)
        {
            proposalName = table.get(i).get(0);

            for(int j = 0 ; j < typeOfEnergy.length; j++)
                if(table.get(i).get(2+j).equals("1"))
                    typeEnergy = typeOfEnergy[j];

            location = table.get(i).get(table.get(0).size() - 1);

            query = "SELECT name FROM user WHERE id="+providerId;
            nameProvider = new Query(query).executeAndGetResult("name").getStringElem(0,0);

            proposalBasics.add(new ProposalBasic(proposalName, providerId, nameProvider, typeEnergy, location));
        }
        String getNbOfProposalQuery = "SELECT count(*) AS 'c' FROM proposal WHERE provider_id="+providerId;
        int count = new Query(getNbOfProposalQuery).executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count, proposalBasics};
    }

    /**
     * Donne toutes les propositions d'une certaine énergie et region dans un intervalle : [base,base+limit]
     * en plus du nombre d'éléments total.
     *
     * @param energyCategory le type d'énergie
     * @param regionCategory la région
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return un tableau contenant en premier indice le nombre d'éléments total et une ArrayList contenant des ProposalBasics.
     */
    public Object[] getAllProposals(String energyCategory, String regionCategory, int base, int limit) //TODO je suis amnésique je pense
    {
        String query = "SELECT * FROM proposal LIMIT "+base+", "+(base+limit);
        if(energyCategory != null && regionCategory != null)
            query = "SELECT * FROM proposal WHERE "+energyCategory+"=1 AND location='"+regionCategory + "' LIMIT "+base+", "+limit;
        else if(energyCategory != null)
            query = "SELECT * FROM proposal WHERE "+energyCategory+"=1 LIMIT "+base+", "+(base+limit);
        else if(regionCategory != null)
            query = "SELECT * FROM proposal WHERE location='"+regionCategory + "' LIMIT "+base+", "+limit;

        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult
                (
                        "proposal_name","provider_id", "water", "gas", "electricity", "location"
                ).getTable();

        String proposalName;
        String providerId;
        String nameProvider;
        String typeEnergy = null;
        String location;

        ArrayList<ProposalBasic> proposalBasics = new ArrayList<>();
        for(int i = 0; i < table.size(); i ++)
        {
            proposalName = table.get(i).get(0);
            providerId = table.get(i).get(1);


            for(int j = 0 ; j < typeOfEnergy.length; j++)
                if(table.get(i).get(2+j).equals("1"))
                    typeEnergy = typeOfEnergy[j];

            location = table.get(i).get(table.get(0).size() - 1);
            nameProvider = new Query("SELECT name FROM user WHERE id="+providerId).executeAndGetResult("name").getStringElem(0,0);
            proposalBasics.add(new ProposalBasic(proposalName, providerId, nameProvider, typeEnergy, location));
        }

        DB.getInstance().executeQuery("SELECT count(*) AS 'c' FROM proposal", true);
        int count = Integer.parseInt(DB.getInstance().getResults("c").get(0).get(0));
        return new Object[] {count,proposalBasics};
    }

    /**
     * Renvoie une proposition en particulier en fonction du nom et de l'identifiant du fournisseur associé.
     *
     * @param proposalName le nom de la proposition
     * @param providerId l'id du fournisseur
     * @return un objet ProposalFull
     */
    public ProposalFull getProposal(String proposalName, String providerId)
    {
        if(!doesTheProposalExist(proposalName, providerId))
            return null;

        ArrayList<ArrayList<String>> table = new Query
                (
                        "SELECT * FROM proposal WHERE proposal_name='"+proposalName+ "' AND provider_id="+providerId
                ).executeAndGetResult
                (
                        "proposal_name","provider_id","water","gas","electricity","fixed_rate",
                        "peak_hours","offpeak_hours","start_peak_hours","end_peak_hours","price","location", "duration"
                ).getTable();

        ProposalFull proposalFull;
        String typeEnergy = null;

        for(int i = 0; i < typeOfEnergy.length; i++)
            if(table.get(0).get(i+2).equals("1"))
                typeEnergy = typeOfEnergy[i];

        boolean fixeRate = table.get(0).get(5).equals("1");
        double peakHours = Double.parseDouble(table.get(0).get(6));
        double offPeakHours = Double.parseDouble(table.get(0).get(7));
        String startPeakHours = table.get(0).get(8);
        String endPeakHours = table.get(0).get(9);
        double basicPrice = Double.parseDouble(table.get(0).get(10));
        String location = table.get(0).get(11);
        String duration = table.get(0).get(12);


        String nameProvider = new Query("SELECT name FROM user WHERE id="+providerId).executeAndGetResult("name").getStringElem(0,0);

        proposalFull = new ProposalFull(providerId, nameProvider, typeEnergy, location, proposalName);
        proposalFull.setMoreInformation(basicPrice, peakHours, offPeakHours, fixeRate, peakHours == offPeakHours, startPeakHours, endPeakHours, Integer.parseInt(duration));
        return proposalFull;
    }

    /**
     * Ajoute une proposition.
     * PRECONDITION: tous les contrats associés à l'ancienne proposition -> inexistant
     *
     * @param proposal objet représentant la proposition à ajouter
     * @return vrai si une ancienne proposition portant le même nom et même identifiant fournisseur est supprimée, faux sinon.
     */
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

        new Query(query).executeWithoutResult();

        return value;
    }

    /**
     * Supprime une proposition.
     * PRECONDITION: tous contrats reliés à cette proposition → inexistant
     *
     * @param proposalName le nom de la proposition
     * @param providerId l'identifiant du fournisseur
     */
    public void deleteProposal(String proposalName, String providerId)
    {
        new Query("DELETE FROM proposal WHERE proposal_name='"+proposalName+"' AND provider_id="+providerId).executeWithoutResult();
    }

}
