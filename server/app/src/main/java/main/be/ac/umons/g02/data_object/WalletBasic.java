package main.be.ac.umons.g02.data_object;

public class WalletBasic
{
    protected String address;
    protected String name;
    protected String clientId;

    public WalletBasic(String address, String name, String nameOwner){
        this.address = address;
        this.name = name;
        this.clientId = nameOwner;
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


}
