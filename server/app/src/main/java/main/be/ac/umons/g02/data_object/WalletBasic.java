package main.be.ac.umons.g02.data_object;

public class WalletBasic
{
    protected String address;
    protected String name; //le nom du portefeuille
    protected String clientId;
    protected  String ownerName; //le nom du propriétaire du portefeuille

    public WalletBasic(String address, String name, String ownerId, String ownerName){
        this.address = address;
        this.name = name;
        this.clientId = ownerId;
        this.ownerName = ownerName;
    }

    public String getAddress()
    {
        return address;
    }

    public String getName()
    {
        return name;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getOwnerName(){return ownerName;}

}
