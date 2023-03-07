package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.WalletBasic;
import main.be.ac.umons.g02.data_object.WalletFull;

import java.util.ArrayList;

public class WalletManager
{
    public enum energyType {ELECTRICITY, WATER, GAS};

    public ArrayList<WalletBasic> getAllWallets(String clientId, int base, int limit)
    {
        //if(!new LogManager().isClient(clientId))
          //  throw new Exception("the client doesn't exist");

        DB.getInstance().executeQuery("SELECT * FROM wallet WHERE id="+clientId+ " LIMIT " + base+", " + (base+limit),true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"address","client_id","wallet_name"});

        //if(results.get(0).size() == 0)
          //  throw new Exception("The client doesn't have any wallet");

        ArrayList<WalletBasic> walletBasics = new ArrayList<>();

        for(int i = 0; i < results.get(0).size(); i++)
        {
            walletBasics.add(new WalletBasic(results.get(0).get(i), results.get(1).get(i), results.get(2).get(i)));
        }

        return walletBasics;
    }


    public WalletFull getWallet(String address)
    {
        if(walletIsEmpty(address))
            return null;
        DB.getInstance().executeQuery("SELECT * FROM wallet WHERE address='"+address+"'",true);

        return null; //TODO pas encore finis
    }

    public boolean createWallet(WalletBasic walletBasic)
    {
        if(!walletIsEmpty(walletBasic.getAddress()))
            return false; //TODO add exception ?

        DB.getInstance().executeQuery("INSERT INTO wallet(address,client_id,wallet_name) VALUES('"+
                walletBasic.getAddress()+"',"+walletBasic.getClientId()+",'"+walletBasic.getName()+"')",false);
        return true;
    }

    public void deleteWallet(String address)
    {
    //    if(!walletIsEmpty(address))
      //      throw new Exception("Cannot remove a wallet while it is not empty");

        DB.getInstance().executeQuery("DELETE FROM wallet WHERE address='"+address+"'", false);
    }

    public boolean walletIsEmpty(String address)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM wallet_contract WHERE address='"+address+"') AS c",true);
        return Integer.parseInt(DB.getInstance().getResults(new String[] {"c"}).get(0).get(0)) == 0;
    }

    public void addLastConsumption(String address,double value, energyType energyType)
    {

        String[] columns = {"latest_consumption_elec", "latest_consumption_water", "latest_consumption_gas"};
        String column = columns[energyType.ordinal()];
        DB.getInstance().executeQuery("UPDATE wallet SET "+column+"="+value+" WHERE address="+address, false);
    }

}
