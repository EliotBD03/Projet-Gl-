package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.InvitedClient;


import java.util.ArrayList;

public class InvitedClientManager
{
    /**
     * Renvoie tous les clients invités suite à une précédente requête faite dans les autres méthodes.
     *
     * @return une ArrayList de tuples clients invités
     * @author Extension Claire
     */
    private ArrayList<InvitedClient> getInvitedClients(ArrayList<ArrayList<String>> results)
    {

        ArrayList<InvitedClient> invitedClients = new ArrayList<>();

        if(results.get(0) == null)
            return null;

        for(int i = 0; i < results.size();i++)
            invitedClients.add(new InvitedClient(results.get(i).get(0), results.get(i).get(1), results.get(i).get(2), results.get(i).get(3)));

        return invitedClients;
    }

    /**
     * Renvoie tous les clients invités d'un propriétaire.
     *
     * @param address l'adresse du portefeuille
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return renvoie tous les clients invités (sur un portefeuille) dans un intervalle [base, base + limit] (une arraylist) et le nombre TOTAL de clients associés au portefeuille
     * @author 
     */
    public Object[] getAllInvitedClients(String address, int base, int limit)
    {   
        String query = "SELECT invitedTable.invitedId, invitedTable.permission, "+
                "user.name AS invitedName, user.mail AS invitedMail " + 
                "FROM invitedTable JOIN user ON invitedTable.invitedId = user.id "+
                "WHERE invitedTable.address = '"+address +"'";

        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("permission", "invitedName", "invitedId", "invitedMail").getTable();

        if(table.equals(Table.EMPTY_TABLE))
            return new Object[] {0, new ArrayList<InvitedClient>()};

        ArrayList<InvitedClient> invitedClients = getInvitedClients(table);

        query = "SELECT COUNT(*) AS 'c' FROM ( SELECT invitedTable.invitedId, invitedTable.permission, "+
                "user.name AS invitedName, user.mail AS invitedMail " + 
                "FROM invitedTable JOIN user ON invitedTable.invitedId = user.id "+
                "WHERE invitedTable.address = '"+address+"') a";
        int count = new Query(query).executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count, invitedClients};
    }

    /**
     * Supprime un client invité d'un portefeuille.
     *
     * @param address l'adresse du portefeuille
     * @param invitedClientId l'id du client invité à supprimer
     * @author Extension Claire
     */
    public void deleteInvitedClient(String address, String invitedClientId)
    {
        String query = "DELETE FROM invitedTable WHERE address = '"+ address +"' AND invitedId = "+ invitedClientId;
        new Query(query).executeWithoutResult();
    }

     /**
     * Le propriétaire ajoute un invité.
     *
     * @param ownerId l'id du propriétaire
     * @param address l'adresse de la maison
     * @param InvitedClientId l'id du client invité
     * @param permission la permission du client invité
     * @return vrai si le client est ajouté, faux si le client était déjà invité
     * @author Extension Claire
     */

    public boolean addInvited(String ownerId, String address, String invitedClientId, String permission)
    {
        if((new Query ("SELECT EXISTS(SELECT * FROM invitedTable WHERE address = '"+address+"' and invitedId = "+invitedClientId +") as C").executeAndGetResult("c").getIntElem(0,0) == 0))
        {
            new Query("INSERT INTO invitedTable(" +
                    " address," +
                    " invitedId," +
                    " ownerId," +
                    " permission )" +
                    " VALUES("+ "'" +
                    address + "'," +
                    invitedClientId + "," +
                    ownerId + ",'" +
                    permission + "')").executeWithoutResult();
            return true;
        }
        return false;
    }

     /**
     * Change le permission d'un client invité
     * @param address l'adresse de la maison
     * @param InvitedClientId l'id du client invité
     * @param permission la permission du client invité
     * @author Extension Claire
     */
    public void changePermission(String address, String invitedClientId, String permission)
    {
        new Query("UPDATE invitedTable SET permission='"+permission+"' WHERE address = '"+address+"' AND invitedId = "+invitedClientId+"").executeWithoutResult();
    }
}
