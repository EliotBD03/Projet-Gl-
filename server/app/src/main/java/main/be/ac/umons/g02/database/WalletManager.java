package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.WalletBasic;
import main.be.ac.umons.g02.data_object.WalletFull;

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
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM wallet WHERE address='"+address+"') AS 'c'",true);
        return Integer.parseInt(DB.getInstance().getResults("c").get(0).get(0)) == 1;
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
    public Object[] getAllWallets(String clientId, int base, int limit)
    {

        DB.getInstance().executeQuery("SELECT * FROM wallet WHERE client_id="+clientId+ " LIMIT " + base+", " + limit,true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults("address","wallet_name","client_id");

        ArrayList<WalletBasic> walletBasics = new ArrayList<>();

        for(int i = 0; i < results.get(0).size(); i++)
        {
            walletBasics.add(new WalletBasic(results.get(0).get(i), results.get(1).get(i), results.get(2).get(i), new LogManager().getName(results.get(2).get(i))));
        }
        DB.getInstance().executeQuery("SELECT count(*) AS 'c' FROM wallet WHERE client_id="+clientId, true);
        int count = Integer.parseInt(DB.getInstance().getResults("c").get(0).get(0));

        return new Object[] {count,walletBasics};
    }

    /**
     * Donne le portefeuille d'une adresse particulière.
     *
     * @param address l'adresse du portefeuille
     * @return objet WalletFull
     */

    public WalletFull getWallet(String address)
    {
        if(walletIsEmpty(address))
            return null;
        DB.getInstance().executeQuery("SELECT * FROM wallet WHERE address='"+address+"'",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(
                "address",
                "wallet_name",
                "client_id",
                "latest_consumption_elec",
                "latest_consumption_water",
                "latest_consumption_gas"
        );
        WalletFull walletFull = new WalletFull(results.get(0).get(0),results.get(1).get(0), results.get(2).get(0), new LogManager().getName(results.get(2).get(0)));
        walletFull.setLastConsumption(Double.parseDouble(results.get(4).get(0)), Double.parseDouble(results.get(3).get(0)), Double.parseDouble(results.get(5).get(0)));
        ArrayList<ContractBasic> contractBasics =(ArrayList<ContractBasic>) new ContractManager().getAllContracts(walletFull.getClientId(), 0, -1)[1];
        walletFull.addContracts(contractBasics);
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

        DB.getInstance().executeQuery("INSERT INTO wallet(address,client_id,wallet_name) VALUES('"+
                walletBasic.getAddress()+"',"+walletBasic.getClientId()+",'"+walletBasic.getName()+"')",false);
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

        DB.getInstance().executeQuery("DELETE FROM wallet WHERE address='"+address+"'", false);
        return true;
    }

    /**
     * Vérifie si le portefeuille est vide. C'est-à-dire qu'il ne possède aucun contrat.
     *
     * @param address l'adresse du portefeuille à supprimer
     * @return vrai si le portefeuille est vide, faux sinon
     */
    public boolean walletIsEmpty(String address)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM wallet_contract WHERE address='"+address+"') AS c",true);
        return Integer.parseInt(DB.getInstance().getResults("c").get(0).get(0)) == 0;
    }

    /**
     * Ajoute la dernière consommation (la plus récente) associées au portefeuille.
     *
     * @param address l'adresse du portefeuille
     * @param value la valeur de la consommation
     * @param energyType le type d'énergie
     */
    public void addLastConsumption(String address,double value, energyType energyType)
    {

        String[] columns = {"latest_consumption_gas", "latest_consumption_water", "latest_consumption_elec"};
        String column = columns[energyType.ordinal()];
        DB.getInstance().executeQuery("UPDATE wallet SET "+column+"="+value+" WHERE address="+address, false);
    }

}
