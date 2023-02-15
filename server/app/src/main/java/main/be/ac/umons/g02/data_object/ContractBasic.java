package main.be.ac.umons.g02.data_object;

public class ContractBasic
{
    protected String contractId;
    protected String ean;
    protected String providerId;
    protected String clientId;
    protected String providerName;
    protected String clientName;


    ContractBasic(String contractId,  String ean, String providerId, String clientId, String providerName, String clientName)
    {
        this.contractId = contractId;
        this.ean = ean;
        this.providerId = providerId;
        this.clientId = clientId;
        this.providerName = providerName;
        this.clientName = clientName;
    }

    public String getContractId()
    {
        return contractId;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getProviderId()
    {
        return providerId;
    }

    public String getEan()
    {
        return ean;
    }

    public String getProviderName()
    {
        return providerName;
    }

    public String getClientName()
    {
        return clientName;
    }
}
