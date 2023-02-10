package main.be.ac.umons.g02.data_object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClientFull extends ClientBasic
{
    private ArrayList<ContractBasic> contracts;
    private HashMap<String,Double> lastConsumptionOfWater;
    private HashMap<String, Double> lastConsumptionOfElectricity;
    private HashMap<String, Double> lastConsumptionOfGas;

    public ClientFull(String id_client, String name, String mail)
    {
        super(id_client, name, mail);
    }

    public ArrayList<ContractBasic> getContracts()
    {
        return (ArrayList<ContractBasic>) Collections.unmodifiableCollection(contracts);
    }

    public void addContracts(ArrayList<ContractBasic> contracts)
    {
        this.contracts.addAll(contracts);
    }

    public HashMap<String, Double> getLastConsumptionOfWater()
    {
        return lastConsumptionOfWater;
    }

    public HashMap<String, Double> getLastConsumptionOfElectricity()
    {
        return lastConsumptionOfElectricity;
    }

    public HashMap<String, Double> getLastConsumptionOfGas()
    {
        return lastConsumptionOfGas;
    }

    public void setLastConsumptionOfWater(String ean, double value)
    {
        lastConsumptionOfWater.put(ean, value);
    }

    public void setLastConsumptionOfElectricity(String ean, double value)
    {
        lastConsumptionOfElectricity.put(ean, value);
    }

    public void setLastConsumptionOfGas(String ean, double value)
    {
        lastConsumptionOfGas.put(ean, value);
    }
} 
