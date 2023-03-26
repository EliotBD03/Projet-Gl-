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
    private ArrayList<ClientBasic> getClientBasics()
    {
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"id", "name", "mail"});

        ArrayList<ClientBasic> clientBasics = new ArrayList<>();
        if(results.get(0) == null)
        {
            return null;
        }

        for(int i = 0; i < results.get(0).size();i++)
        {
            clientBasics.add(new ClientBasic(results.get(0).get(i), results.get(1).get(i), results.get(2).get(i)));
        }

        return clientBasics;
    }

    /**
     * Renvoie n'importe quel client de la table client.
     *
     *  @param base la borne inférieure
     * @param limit le nombre d'élément
     * @return renvoie tous les clients dans un intervalle [base, base + limit] (une arraylist) en plus de la taille de la table
     */
    public Object[] getAllClients(int base, int limit)
    {
        DB.getInstance().executeQuery("SELECT * FROM user WHERE id IN (SELECT id FROM client) LIMIT "+base+", "+limit,true);
        ArrayList<ClientBasic> clientBasics =  getClientBasics();
        DB.getInstance().executeQuery("SELECT count(*) AS c FROM client", true);
        int count = Integer.parseInt(DB.getInstance().getResults(new String[] {"c"}).get(0).get(0));
        return new Object[] {count, clientBasics};
    }

    /**
     * Renvoie tous les clients d'un fournisseur.
     *
     *  @param providerId l'identifiant du fournisseur
     * @param base la borne inférieure
     * @param limit le nombre d'élément
     * @return renvoie tous les clients dans un intervalle [base, base + limit] (une arraylist) et le nombre TOTAL de clients associés au fournisseur
     */
    public Object[] getAllHisClients(String providerId, int base, int limit)
    {
        DB.getInstance().executeQuery("SELECT * FROM user WHERE id IN "+
                "(SELECT client_id FROM wallet WHERE address IN "+
                "(SELECT address FROM wallet_contract WHERE contract_id IN "+
                "(SELECT contract_id FROM provider_contract WHERE provider_id="+providerId+"))) "+
                "LIMIT "+base+", "+limit,true);
        ArrayList<ClientBasic> clientBasics = getClientBasics();
        DB.getInstance().executeQuery("SELECT count(*) AS c FROM user WHERE id IN "+
                "(SELECT client_id FROM wallet WHERE address IN "+
                "(SELECT address FROM wallet_contract WHERE contract_id IN "+
                "(SELECT contract_id FROM provider_contract WHERE provider_id="+providerId+"))) ",true);
        int count = Integer.parseInt(DB.getInstance().getResults(new String[] {"c"}).get(0).get(0));
        return new Object[] {count, clientBasics};
    }

    /**
     * Supprime un client d'un fournisseur.
     *
     *  @param providerId l'id du fournisseur
     * @param clientId l'id du client à supprimer
     */
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
