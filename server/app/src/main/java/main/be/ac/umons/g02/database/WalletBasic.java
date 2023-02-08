package main.be.ac.umons.g02.database;

public class WalletBasic
{
    protected String address;
    protected String name;
    protected String nameOwner;

    public WalletBasic(String address, String name, String nameOwner){
        this.address = address;
        this.name = name;
        this.nameOwner = nameOwner;
    }

    public String getAddress()
    {
        return address;
    }

    public String getName()
    {
        return name;
    }

    public String getNameOwner()
    {
        return nameOwner;
    }


}
