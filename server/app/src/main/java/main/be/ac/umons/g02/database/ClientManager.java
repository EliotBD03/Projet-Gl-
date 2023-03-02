package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ClientBasic;
import main.be.ac.umons.g02.data_object.ClientFull;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Objects;

public class ClientManager
{
    public ArrayList<ClientBasic> getAllClients(int base, int limit)
    {
        DB.getInstance().executeQuery("SELECT * FROM user WHERE id in(SELECT id FROM client limit "+base+", "+(limit - base) + ")",true);
        return getClientBasics();
    }

    public ArrayList<ClientBasic> getAllHisClients(String providerId, int base, int limit)
    {
        DB.getInstance().executeQuery("SELECT * FROM user WHERE id in(SELECT id FROM client limit "+base+", "+(limit - base) + ")"+
                "AND id IN (SELECT id FROM contract WHERE provider_id="+providerId+")",true);

        return getClientBasics();
    }

    private ArrayList<ClientBasic> getClientBasics()
    {
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"id", "mail", "name"});

        ArrayList<ClientBasic> clientBasics = new ArrayList<>();
        if(results.get(0) == null)
            return null;
        for(int i = 0; i < results.get(0).size();i++)
        {
            clientBasics.add(new ClientBasic(results.get(0).get(i), results.get(1).get(i), results.get(2).get(i)));
        }

        return clientBasics;
    }

    public ClientFull getClient(String clientId)
    {
        DB.getInstance().executeQuery("SELECT * FROM user WHERE id="+clientId, true);
        ClientBasic temp = Objects.requireNonNull(getClientBasics()).get(0);
        ClientFull clientFull = new ClientFull(temp.getClientId(), temp.getName(), temp.getMail());
        DB.getInstance().executeQuery("SELECT * FROM client WHERE id="+clientId, true);
        ArrayList<ArrayList<String>> latestConsumptions = DB.getInstance().getResults(new String[] {"latest_consumption_elec","latest_consumption_water", "latest_consumption_gas"});
        //DB.getInstance().executeQuery("")//TODO doit passer par les méthodes de contractBasic
        return null;

    }
    
    public void deleteClient(String providerId, String clientId)
    {
        DB.getInstance().executeQuery("SELECT contract_id FROM contract WHERE provider_id="+providerId+ " AND client_id=" +clientId, true);
        ArrayList<String> contracts = DB.getInstance().getResults(new String [] {"address"}).get(0);
        for(int i = 0; i < contracts.size(); i++)
        {
            DB.getInstance().executeQuery("DELETE FROM wallet_contract WHERE contract_id="+contracts.get(i), false);
            DB.getInstance().executeQuery("DELETE FROM provider_contract WHERE contract_id="+contracts.get(i), false);
            DB.getInstance().executeQuery("DELETE FROM counter WHERE contract_id="+contracts.get(i), false);
            DB.getInstance().executeQuery("DELETE FROM contract WHERE contract_id="+contracts.get(i), false);
        }
    }
}
