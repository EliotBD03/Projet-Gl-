package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.ContractFull;
import main.be.ac.umons.g02.data_object.ProposalBasic;
import org.checkerframework.checker.units.qual.A;

import java.beans.BeanDescriptor;
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

    public ArrayList<ContractBasic> getAllContracts(String clientId)
    {
        return null; //TODO
    }
}
