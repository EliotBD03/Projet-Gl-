package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.WalletBasic;
import main.be.ac.umons.g02.data_object.WalletFull;

import java.util.ArrayList;

public class WalletManager
{
    public enum energyType {WATER, GAS, ELECTRICITY};


    public boolean doesTheWalletExists(String address)
    {
       return DB.getInstance().isThereSomething("wallet", new String[] {"address"}, new String[] {"'" +address+ "'"});
    }
    public Object[] getAllWallets(String clientId, int base, int limit)
    {
        //if(!new LogManager().isClient(clientId))
          //  throw new Exception("the client doesn't exist");


        DB.getInstance().executeQuery("SELECT * FROM wallet WHERE client_id="+clientId+ " LIMIT " + base+", " + (base+limit),true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"address","wallet_name","client_id"});

        //if(results.get(0).size() == 0)
          //  throw new Exception("The client doesn't have any wallet");

        ArrayList<WalletBasic> walletBasics = new ArrayList<>();

        for(int i = 0; i < results.get(0).size(); i++)
        {
            walletBasics.add(new WalletBasic(results.get(0).get(i), results.get(1).get(i), results.get(2).get(i)));
        }
        DB.getInstance().executeQuery("SELECT count(*) AS 'c' FROM wallet WHERE client_id="+clientId, true);
        int count = Integer.parseInt(DB.getInstance().getResults(new String[] {"c"}).get(0).get(0));

        return new Object[] {count,walletBasics};
    }


    public WalletFull getWallet(String address)
    {
        if(walletIsEmpty(address))
            return null;
        DB.getInstance().executeQuery("SELECT * FROM wallet WHERE address='"+address+"'",true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {
                "address",
                "wallet_name",
                "client_id",
                "latest_consumption_elec",
                "latest_consumption_water",
                "latest_consumption_gas"
        });
        WalletFull walletFull = new WalletFull(results.get(0).get(0),results.get(1).get(0), results.get(2).get(0));
        walletFull.setLastConsumption(Double.parseDouble(results.get(4).get(0)), Double.parseDouble(results.get(3).get(0)), Double.parseDouble(results.get(5).get(0)));
        return walletFull; //TODO pas encore finis
    }

    public boolean createWallet(WalletBasic walletBasic)
    {
        if(!walletIsEmpty(walletBasic.getAddress()))
            return false; //TODO add exception ?

        DB.getInstance().executeQuery("INSERT INTO wallet(address,client_id,wallet_name) VALUES('"+
                walletBasic.getAddress()+"',"+walletBasic.getClientId()+",'"+walletBasic.getName()+"')",false);
        return true;
    }

    public boolean deleteWallet(String address)
    {
        if(walletIsEmpty(address))
            return false;

        DB.getInstance().executeQuery("DELETE FROM wallet WHERE address='"+address+"'", false);
        return true;
    }

    public boolean walletIsEmpty(String address)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM wallet WHERE address='"+address+"') AS c",true);
        return Integer.parseInt(DB.getInstance().getResults(new String[] {"c"}).get(0).get(0)) == 0;
    }

    public void addLastConsumption(String address,double value, energyType energyType)
    {

        String[] columns = {"latest_consumption_elec", "latest_consumption_water", "latest_consumption_gas"};
        String column = columns[energyType.ordinal()];
        DB.getInstance().executeQuery("UPDATE wallet SET "+column+"="+value+" WHERE address="+address, false);
    }

}
