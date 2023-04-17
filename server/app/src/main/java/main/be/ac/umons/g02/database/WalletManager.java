package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.WalletBasic;
import main.be.ac.umons.g02.data_object.WalletFull;
import main.be.ac.umons.g02.data_object.InvitedClient;

import java.util.ArrayList;

public class WalletManager
{
    public enum energyType {WATER, GAS, ELECTRICITY};

    /**
     * Vérifie si le portefeuille existe.
     *
     * @param address l'adresse du portefeuille
     * @return vrai si le portefeuille existe dans la base de données, faux sinon.
     */
    public boolean doesTheWalletExists(String address)
    {
        return new Query("SELECT EXISTS(SELECT * FROM wallet WHERE address='"+address+"') AS 'c'").executeAndGetResult("c").getIntElem(0,0) == 1;
    }

    /**
     * Donne tous les portefeuilles d'un certain client dans un intervalle : [base, base + limit]
     * en plus du nombre total de portefeuilles que le client possède.
     *
     * @param clientId l'identifiant du client
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return un tableau reprenant le nombre total en premier indice et une ArrayList d'objets WalletBasic.
     */
    @SuppressWarnings("removal")
    public Object[] getAllWallets(String clientId, int base, int limit)
    {
        String query = "SELECT * FROM wallet WHERE client_id="+clientId+ " LIMIT " + base+", " + limit;
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("address", "wallet_name", "client_id", "number_of_residents", "size_of_house", "is_house", "is_electricity_to_charge", "solar_panels").getTable();

        ArrayList<WalletBasic> walletBasics = new ArrayList<>();

        for (ArrayList<String> row : table)
            walletBasics.add(new WalletBasic(row.get(0), row.get(1), row.get(2), new LogManager().getName(row.get(2)), new Integer(row.get(3)), new Integer(row.get(4)), row.get(5).equals("1"), row.get(6).equals("1"), row.get(7).equals("1")));

        int count = new Query("SELECT count(*) AS 'c' FROM wallet WHERE client_id="+clientId).executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count,walletBasics};
    }

    /**
     * Donne tous les portefeuilles où un certain client est invité dans un intervalle : [base, base + limit]
     * en plus du nombre total de portefeuilles que le client possède.
     *
     * @param clientId l'identifiant du client invité
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return un tableau reprenant le nombre total en premier indice et une ArrayList d'objets WalletBasic.
     * @author Extension Claire
     */
    public Object[] getAllInvitedWallets(String clientId, int base, int limit)
    {
        String query = "SELECT w.*, inv.permission, ROW_NUMBER() OVER () AS row_number "+
                        "FROM wallet w " +
                        "JOIN invitedTable inv ON w.address COLLATE utf8mb4_unicode_ci = inv.address "+
                        "WHERE inv.invitedId = "+ clientId +" LIMIT " + base+", " + limit;


        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("address", "wallet_name", "client_id", "permission").getTable();

        ArrayList<WalletBasic> walletBasics = new ArrayList<>();

        for (ArrayList<String> row : table)
            walletBasics.add(new WalletBasic(row.get(0), row.get(1), row.get(2), new LogManager().getName(row.get(2)), row.get(3)));

        int count = new Query("SELECT count(*) AS 'c' FROM" +" (SELECT w.*, inv.permission, ROW_NUMBER() OVER () AS row_number "+
                        "FROM wallet w " +
                        "JOIN invitedTable inv ON w.address COLLATE utf8mb4_unicode_ci = inv.address "+
                        "WHERE inv.invitedId = "+ clientId + ") p").executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count,walletBasics};
    }

    /**
     * Donne le portefeuille d'une adresse particulière.
     *
     * @param address l'adresse du portefeuille
     * @return objet WalletFull
     */
    @SuppressWarnings("removal")
    public WalletFull getWallet(String address)
    {
        if(!doesTheWalletExists(address))
            return null;

        String query = "SELECT * FROM wallet WHERE address='"+address+"'";
        DB.getInstance().executeQuery("SELECT * FROM wallet WHERE address='"+address+"'",true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "address",
                "wallet_name",
                "client_id",
                "latest_consumption_elec",
                "latest_consumption_water",
                "latest_consumption_gas",
                "number_of_residents",
                "size_of_house",
                "is_house",
                "is_electricity_to_charge",
                "solar_panels"
        ).getTable();

        ArrayList<String> row = table.get(0);
        WalletFull walletFull = new WalletFull(row.get(0), row.get(1), row.get(2), new LogManager().getName(row.get(2)), new Integer(row.get(6)), new Integer(row.get(7)), row.get(8).equals("1"), row.get(9).equals("1"), row.get(10).equals("1"));

        walletFull.setLastConsumption(Double.parseDouble(row.get(4)), Double.parseDouble(row.get(3)), Double.parseDouble(row.get(5)));

        ArrayList<ContractBasic> contractBasics =(ArrayList<ContractBasic>) new ContractManager().getAllContracts(walletFull.getClientId(), 0, -1)[1];
        walletFull.addContracts(contractBasics);

        ArrayList<InvitedClient> invitedClients =(ArrayList<InvitedClient>) new InvitedClientManager().getAllInvitedClients(walletFull.getAddress(), 0, -1)[1];
        walletFull.addInvitedClients(invitedClients); //Extension Claire

        return walletFull;
    }

    /**
     * Crée un portefeuille dans la base de données.
     *
     * @param walletBasic le portefeuille à créer
     * @return vrai si le portefeuille a été créé, faux sinon (portefeuille existe déjà)
     */
    public boolean createWallet(WalletBasic walletBasic)
    {
        if(doesTheWalletExists(walletBasic.getAddress()))
            return false;

        new Query("INSERT INTO wallet(address,client_id,wallet_name, number_of_residents, size_of_house, is_house, is_electricity_to_charge, solar_panels) VALUES('"+
                walletBasic.getAddress()+"',"+
                walletBasic.getClientId()+",'"+
                walletBasic.getName()+"','"+
                walletBasic.getNumberOfResidents()+"','"+
                walletBasic.getSizeOfHouse()+"','"+
                (walletBasic.getIsHouse() ? 1 : 0)+"','"+
                (walletBasic.getIsElectricityToCharge() ? 1 : 0)+"','"+
                (walletBasic.getSolarPanels() ? 1 : 0)+"')")
          .executeWithoutResult();

        return true;
    }

    /**
     * Supprime un portefeuille.
     *
     * @param address l'adresse du portefeuille associé
     * @return vrai si le portefeuille a été supprimé, faux sinon (portefeuille n'est pas vide).
     */
    public boolean deleteWallet(String address)
    {
        if(!walletIsEmpty(address))
            return false;

        new Query("DELETE FROM wallet WHERE address='"+address+"'").executeWithoutResult();
        return true;
    }

    /**
     * Vérifie si le portefeuille est vide. C'est-à-dire qu'il ne possède aucun contrat.
     * Vérifie également s'il n'y a plus de clients invités sur le portefeuille. -> Extension Claire
     *
     * @param address l'adresse du portefeuille à supprimer
     * @return vrai si le portefeuille est vide, faux sinon
     */
    public boolean walletIsEmpty(String address)
    {
        return new Query("SELECT EXISTS(SELECT * FROM wallet_contract WHERE address='"+address+"') AS c")
                .executeAndGetResult("c")
                .getIntElem(0,0) == 0 && new Query("SELECT EXISTS(SELECT * FROM invitedTable WHERE address='"+address+"') AS i")
                .executeAndGetResult("i")
                .getIntElem(0,0) == 0;
    }

    /**
     * Ajoute la dernière consommation (la plus récente) associées au portefeuille.
     *
     * @param address l'adresse du portefeuille
     * @param value la valeur de la consommation
     * @param energyType le type d'énergie
     */
    public void addLastConsumption(String address, String clientId,double value, energyType energyType)
    {

        String[] columns = {"latest_consumption_gas", "latest_consumption_water", "latest_consumption_elec"};
        String column = columns[energyType.ordinal()];
        new Query("UPDATE wallet SET "+column+"="+value+" WHERE address="+address +" AND client_id="+clientId).executeWithoutResult();
    }

    /**
     * Vérifie si le client possède bien ce portefeuille.
     *
     * @param id l'identifiant du client
     * @param address l'adresse du portefeuille
     * @return vrai s'il lui appartient, faux sinon
     */
    public boolean doesTheWalletBelongToHim(String id, String address)
    {
        return new Query("SELECT EXISTS(SELECT * FROM wallet WHERE address='"+address+"' AND client_id="+id+") AS 'c'")
                .executeAndGetResult("c")
                .getIntElem(0,0) == 1;
    }

}
