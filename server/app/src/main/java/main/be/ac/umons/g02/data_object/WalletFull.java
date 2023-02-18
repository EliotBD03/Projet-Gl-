package main.be.ac.umons.g02.data_object;

import java.util.ArrayList;
import java.util.Collections;

public class WalletFull extends WalletBasic{

    private ArrayList<ContractBasic> contracts;
    private double lastConsumptionOfWater;
    private double lastConsumptionOfElectricity;
    private double lastConsumptionOfGas;

    public WalletFull(String address, String name, String nameOwner)
    {
        super(address, name, nameOwner);
    }

    public ArrayList<ContractBasic> getContracts()
    {
        return (ArrayList<ContractBasic>) Collections.unmodifiableList(contracts);
    }

    public void addContracts(ArrayList<ContractBasic> contracts)
    {
        this.contracts.addAll(contracts);
    }

    public double getLastConsumptionOfWater()
    {
        return lastConsumptionOfWater;
    }

    public double getLastConsumptionOfElectricity()
    {
        return lastConsumptionOfElectricity;
    }

    public double getLastConsumptionOfGas()
    {
        return lastConsumptionOfGas;
    }
}


