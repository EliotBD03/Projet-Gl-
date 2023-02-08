package main.be.ac.umons.g02.data_object;

public class ContractBasic
{
    protected String id_contract;
    protected String id_client;
    protected String id_provider;
    protected String nameProvider;
    protected String nameClient;
    protected String ean;
    protected TypeEnergy typeofEnery;

    ContractBasic(String id_contract, String id_client, String id_provider, String nameProvider, String nameClient, String ean, TypeEnergy typeofEnery)
    {
        this.id_contract = id_contract;
        this.id_client = id_client;
        this.id_provider = id_provider;
        this.nameProvider = nameProvider;
        this.nameClient = nameClient;
        this.ean = ean;
        this.typeofEnery = typeofEnery;
    }

    public String getId_contract()
    {
        return id_contract;
    }

    public String getId_client()
    {
        return id_client;
    }

    public String getId_provider()
    {
        return  id_provider;
    }

    public String getNameProvider()
    {
        return nameProvider;
    }

    public String getNameClient()
    {
        return nameClient;
    }

    public String getEan()
    {
        return ean;
    }
}
