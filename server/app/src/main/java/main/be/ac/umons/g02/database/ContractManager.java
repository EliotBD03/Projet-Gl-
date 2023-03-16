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

        String query = "SELECT * FROM contract WHERE contract_id="+contractId;
        DB.getInstance().executeQuery("SELECT * FROM contract WHERE contract_id="+contractId,true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[]
                {
                "contract_id","proposal_name", "ean","provider_id","address", "client_id", "opening_date", "closing_date"
                });
        ContractFull contract;
        String providerId = results.get(3).get(0);
        ProposalFull proposalFull = commonDB.getProposalManager().getProposal(results.get(1).get(0), providerId);
        String ean = results.get(2).get(0);
        String address = results.get(4).get(0);
        String clientId = results.get(5).get(0);
        String providerName = commonDB.getLogManager().getName(providerId);
        String clientName = commonDB.getLogManager().getName(clientId);
        String openingDate = results.get(6).get(0);
        String closingDate = results.get(7).get(0);
        contract = new ContractFull(contractId,ean,providerId,clientId,providerName,clientName);
        contract.setMoreInformation(proposalFull,openingDate,closingDate, address);
        return contract;
    }

    public ArrayList<ContractBasic> getAllContracts(String clientId, int base, int limit)
    {
        String query = "SELECT * FROM contract WHERE client_id=" + clientId +" LIMIT "+base + ", " + (limit + base);
        if(base < 0)
            query = "SELECT * FROM contract WHERE client_id=" + clientId +" LIMIT "+base + ", " + "18446744073709551615"; //18446744073709551615 max(BigInt) in mysql

        DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+clientId,true);
        String clientName = DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);

        DB.getInstance().executeQuery(query, true);

        ArrayList<ContractBasic> contractBasics = new ArrayList<>();
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"contract_id", "ean",
                "provider_id", "client_id"});

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

    public ArrayList<ContractBasic> getCommonContracts(String clientId, String providerId, int base, int limit)
    {
        ArrayList<ContractBasic> contractBasics = getAllContracts(clientId, base, limit);
        ArrayList<ContractBasic> results = new ArrayList<>();
        for(int i = 0; i < contractBasics.size(); i++)
            if(contractBasics.get(i).getProviderId().equals(providerId))
                results.add(contractBasics.get(i));
        return results;
    }

    public void deleteContract(String contractId)
    {
        DB.getInstance().executeQuery("DELETE FROM contract WHERE contract_id="+contractId, false);
        DB.getInstance().executeQuery("DELETE FROM counter WHERE contract_id="+contractId, false);
        DB.getInstance().executeQuery("DELETE FROM provider_contract WHERE contract_id="+contractId,false);
        DB.getInstance().executeQuery("DELETE FROM wallet_contract WHERE contract_id="+contractId,false);
    }

    public ArrayList<String> getAllClientsOfContract(String proposalName, String providerId)
    {
        DB.getInstance().executeQuery("SELECT client_id FROM contract WHERE proposal_name='"+proposalName
        +"' AND provider_id="+providerId,true);
        return DB.getInstance().getResults(new String[] {"client_id"}).get(0);
    }

    public boolean createContract(String proposalName, String ean, String providerId, String address, String clientId)
    {
        if(!new ProposalManager().doesTheProposalExist(proposalName, providerId) || !new WalletManager().doesTheWalletExists(address))
            return false;

        DB.getInstance().executeQuery("INSERT INTO wallet_contract(address) VALUES('"+address+"')", false);
        DB.getInstance().executeQuery("INSERT INTO provider_contract(provider_id) VALUES("+providerId+")",false);
        DB.getInstance().executeQuery("SELECT max(contract_id) AS m FROM provider_contract", true);

        String contractId = DB.getInstance().getResults(new String[] {"m"}).get(0).get(0);
        String openingDate = "CURDATE()";
        String closingDate = "DATE_ADD(CURDATE(), INTERVAL "+new ProposalManager().getProposal(proposalName, providerId).getDuration()+" MONTH)";

        new ConsumptionManager().createCounterOrReplace(ean, contractId);

        DB.getInstance().executeQuery("INSERT INTO contract(" +
                " proposal_name," +
                " ean," +
                " provider_id," +
                " address," +
                " client_id," +
                " opening_date," +
                " closing_date)" +
                " VALUES('"+proposalName+"','" +
                ean + "'," +
                providerId + ",'" +
                address + "'," +
                clientId + "," +
                openingDate + "," +
                closingDate + ")",false);
        return true;
    }

    public WalletManager.energyType getTypeOfEnergy(String address)
    {
        DB.getInstance().executeQuery("SELECT water, gas, electricity "
                +"FROM proposal "
                +"WHERE (proposal_name,provider_id) "
                +"IN (SELECT proposal_name, provider_id FROM contract WHERE address='"+address + "')",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"water","gas","electricity"});
        for(int i = 0; i < 3 ; i++)
        {
            if(results.get(i).get(0).equals("1"))
                return WalletManager.energyType.values()[i];
        }
        return null;
    }

}
