package main.be.ac.umons.g02.data_object;

public class WalletBasic
{
    protected String address;
    protected String name; //le nom du portefeuille
    protected String clientId;
    protected String ownerName; //le nom du propriétaire du portefeuille
    protected int numberOfResidents;
    protected int sizeOfHouse;
    protected boolean isHouse;
    protected boolean isElectricityToCharge;
    protected boolean solarPanels;

    public WalletBasic(String address, String name, String ownerId, String ownerName, int numberOfResidents, int sizeOfHouse, boolean isHouse, boolean isElectricityToCharge, boolean solarPanels){
        this.address = address;
        this.name = name;
        this.clientId = ownerId;
        this.ownerName = ownerName;
        this.numberOfResidents = numberOfResidents;
        this.sizeOfHouse = sizeOfHouse;
        this.isHouse = isHouse;
        this.isElectricityToCharge = isElectricityToCharge;
        this.solarPanels = solarPanels;
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

    public int getNumberOfResidents()
    {
        return numberOfResidents;
    }

    public int getSizeOfHouse()
    {
        return sizeOfHouse;
    }
    
    public boolean getIsHouse()
    {
        return isHouse;
    }

    public boolean getIsElectricityToCharge()
    {
        return isElectricityToCharge;
    }

    public boolean getSolarPanels()
    {
        return solarPanels;
    }
}
