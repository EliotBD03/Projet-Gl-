package main.be.ac.umons.g02.database;


import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.ContractFull;
import main.be.ac.umons.g02.data_object.ProposalFull;


import java.util.ArrayList;

public class ContractManager
{
    public ContractFull getContract(String contractId)
    {
        CommonDB commonDB = new CommonDB();

        DB.getInstance().executeQuery("SELECT * FROM contract WHERE contract_id="+contractId,true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[]
                {
                "contract_id","proposal_name", "ean","provider_id","address", "client_id", "opening_date", "closing_date"
                });
        ContractFull contract;
        String providerId = results.get(3).get(0);
        ProposalFull proposalFull = commonDB.getProposalManager().getProposal(results.get(1).get(0), providerId);
        String ean = results.get(2).get(0);
        String clientId = results.get(5).get(0);
        String providerName = commonDB.getLogManager().getName(providerId);
        String clientName = commonDB.getLogManager().getName(clientId);
        String openingDate = results.get(6).get(0);
        String closingDate = results.get(7).get(0);
        contract = new ContractFull(contractId,ean,providerId,clientId,providerName,clientName);
        contract.setMoreInformation(proposalFull,openingDate,closingDate);
        return contract;
    }

    public ArrayList<ContractBasic> getAllContracts(String clientId, int base, int limit)
    {
        String query = "SELECT * FROM contract WHERE client_id=" + clientId +" LIMIT "+base + ", " + (limit + base);
        if(base < 0)
            query = "SELECT * FROM contract WHERE client_id=" + clientId +" LIMIT "+base + ", " + "18446744073709551615"; //18446744073709551615 max(BigInt) in mysql

        DB.getInstance().executeQuery(query, true);

        ArrayList<ContractBasic> contractBasics = new ArrayList<>();
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"contract_id", "ean",
                "provider_id", "client_id"});

        DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+clientId,true);
        String clientName = DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);

        String providerId;
        String providerName;
        String contractId;
        String ean;
        for(int i = 0; i < results.get(0).size(); i++)
        {
            providerId = results.get(2).get(i);
            DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+providerId, true);
            providerName = DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);
            contractId = results.get(0).get(i);
            ean = results.get(1).get(i);
            contractBasics.add(new ContractBasic(contractId,ean, providerId, clientId, providerName, clientName));
        }
        return contractBasics;
    }

    public ArrayList<ContractBasic> getCommonContracts(String clientId, String providerId)
    {
        ArrayList<ContractBasic> contractBasics = getAllContracts(clientId, 0, -1);
        ArrayList<ContractBasic> results = new ArrayList<>();
        for(int i = 0; i < contractBasics.size(); i++)
            if(contractBasics.get(i).getProviderId().equals(providerId))
                results.add(contractBasics.get(i));
        return results;
    }

    public void deleteContract(String contractId)
    {
        DB.getInstance().executeQuery("DELETE FROM provider_contract WHERE contract_id="+contractId,false);
        DB.getInstance().executeQuery("DELETE FROM wallet_contract WHERE contract_id="+contractId,false);
        DB.getInstance().executeQuery("DELETE FROM counter WHERE contract_id="+contractId, false);
        DB.getInstance().executeQuery("DELETE FROM contract WHERE contract_id="+contractId, false);
    }

    public ArrayList<String> getAllClientsOfContract(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("SELECT client_id FROM contract WHERE proposal_name='"+proposalName
        +"' AND provider_id="+providerId,true);
        return DB.getInstance().getResults(new String[] {"client_id"}).get(0);
    }

}
