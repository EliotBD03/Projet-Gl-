package main.be.ac.umons.g02.data_object;

import java.util.ArrayList;

public class WalletFull extends WalletBasic{

    private ArrayList<ContractBasic> contracts;
    private double lastConsumptionOfWater;
    private double lastConsumptionOfElectricity;
    private double lastConsumptionOfGas;
    private ArrayList<InvitedClient> invitedClients;

    public WalletFull(String address, String name, String ownerId, String ownerName, int numberOfResidents, int sizeOfHouse, boolean isHouse, boolean isElectricityToCharge, boolean solarPanels){
        super(address, name, ownerId, ownerName, numberOfResidents, sizeOfHouse, isHouse, isElectricityToCharge, solarPanels);
        contracts = new ArrayList<>();
        invitedClients = new ArrayList<>(); //Ajout de la liste des invités Extension Claire
    }


    public void addInvitedClients(ArrayList<InvitedClient> invitedClients)
    {
        this.invitedClients.addAll(invitedClients);
    }


    public void addContracts(ArrayList<ContractBasic> contracts)
    {
        this.contracts.addAll(contracts);
    }

    public void setLastConsumption(double lastConsumptionOfWater, double lastConsumptionOfElectricity, double lastConsumptionOfGas)
    {
        this.lastConsumptionOfWater = lastConsumptionOfWater;
        this.lastConsumptionOfElectricity = lastConsumptionOfElectricity;
        this.lastConsumptionOfGas = lastConsumptionOfGas;
    }
}


