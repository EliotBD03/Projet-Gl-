package main.be.ac.umons.g02.database;


import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.ContractFull;


import java.util.ArrayList;

public class ContractManager
{
    public ContractFull getContract(String contractId)
    {
        /*
        TODO
        DB.getInstance().executeQuery("SELECT * FROM contract WHERE contract_id="+contractId,true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[]
                {
                "contract_id","proposal_name", "ean","provider_id","address", "client_id", "opening_date", "closing_date"
                });
        ContractFull contract;
        for(int i = 0; i < results.size(); i++)
        {
            contract =
        }
         */
        return null;
    }

    public ArrayList<ContractBasic> getAllContracts(String clientId, int base, int limit)
    {
        return null; //TODO
    }

    public void deleteContract(String contractId)
    {

    }

    public ArrayList<String> getAllClientsOfContract(String proposalName, String providerId)
    {
        return null;
    }

    public void providerProposeContract(String proposalName, String providerId, String clientId)
    {
        
    }
}
