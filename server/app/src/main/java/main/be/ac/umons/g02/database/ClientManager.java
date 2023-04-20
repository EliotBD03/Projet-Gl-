package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ClientBasic;


import java.util.ArrayList;

public class ClientManager
{
    /**
     * Renvoie tous les clients suites à une précédente requête faite dans les autres méthodes.
     *
     *  @return une ArrayList de tuples clients
     */
    private ArrayList<ClientBasic> getClientBasics(ArrayList<ArrayList<String>> results)
    {

        ArrayList<ClientBasic> clientBasics = new ArrayList<>();

        if(results.get(0) == null)
            return null;

        for(int i = 0; i < results.size();i++)
            clientBasics.add(new ClientBasic(results.get(i).get(0), results.get(i).get(1), results.get(i).get(2)));

        return clientBasics;
    }

    /**
     * Renvoie n'importe quel client de la table client.
     *
     *  @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return renvoie tous les clients dans un intervalle [base, base + limit] (une arraylist) en plus de la taille de la table
     */
    public Object[] getAllClients(int base, int limit)
    {
        String query = "SELECT * FROM user WHERE id IN (SELECT client_id FROM client) LIMIT "+base+", "+limit;
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("id", "name", "mail").getTable();

        if(table.equals(Table.EMPTY_TABLE))
            return new Object[] {0, Table.EMPTY_TABLE};

        ArrayList<ClientBasic> clientBasics = getClientBasics(table);

        int count = new Query("SELECT count(*) AS c FROM client").executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count, clientBasics};
    }

    /**
     * Renvoie tous les clients d'un fournisseur.
     *
     *  @param providerId l'identifiant du fournisseur
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return renvoie tous les clients dans un intervalle [base, base + limit] (une arraylist) et le nombre TOTAL de clients associés au fournisseur
     */
    public Object[] getAllHisClients(String providerId, int base, int limit)
    {
        String query = "SELECT * FROM user WHERE id IN "+
                "(SELECT client_id FROM wallet WHERE address IN "+
                "(SELECT address FROM wallet_contract WHERE contract_id IN "+
                "(SELECT contract_id FROM provider_contract WHERE provider_id="+providerId+"))) "+
                "LIMIT "+base+", "+limit;

        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("id", "name", "mail").getTable();

        if(table.equals(Table.EMPTY_TABLE))
            return new Object[] {0, new ArrayList<ClientBasic>()};

        ArrayList<ClientBasic> clientBasics = getClientBasics(new Query(query).executeAndGetResult("id", "name", "mail").getTable());

        query = "SELECT count(*) AS c FROM user WHERE id IN "+
                "(SELECT client_id FROM wallet WHERE address IN "+
                "(SELECT address FROM wallet_contract WHERE contract_id IN "+
                "(SELECT contract_id FROM provider_contract WHERE provider_id="+providerId+"))) ";
        int count = new Query(query).executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count, clientBasics};
    }

    /**
     * Supprime un client d'un fournisseur.
     *
     *  @param providerId l'id du fournisseur
     * @param clientId l'id du client à supprimer
     */

    /*
    SELECT a.contract_id FROM (SELECT * FROM provider_contract WHERE provider_id=2) a INNER JOIN (SELECT * FROM wallet_contract WHERE address IN (SELECT address FROM wallet WHERE client_id=1)) b ON a.contract_id = b.contract_id;
     */
    public void deleteClient(String providerId, String clientId)
    {
        String query = "SELECT a.contract_id FROM (SELECT * FROM provider_contract WHERE provider_id="+providerId+") a"+
                " INNER JOIN (SELECT * FROM wallet_contract WHERE address IN (SELECT address FROM wallet WHERE client_id=" +clientId+")) b"+
                " ON a.contract_id = b.contract_id";
        ArrayList<String> contracts = new Query(query).executeAndGetResult("contract_id").getColumn(0);

        String[] tables = {"wallet_contract", "provider_contract", "counter", "contract"};

        for (String contract : contracts)
            new ContractManager().deleteContractAndNotify(contract, providerId);
    }
}
