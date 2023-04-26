package main.be.ac.umons.g02.data_object;

public class ContractFull extends ContractBasic
{
    private ProposalFull proposal;
    private String address;
    private String openingDate;
    private String closingDate;

    public ContractFull(String contractId,  String ean,  String providerId, String clientId, String providerName, String clientName)
    {
        super(contractId, ean, providerId, clientId, providerName, clientName);
    }

    public ProposalFull getProposal(){return proposal;}

    public String getAddress(){return address;}

    public void setMoreInformation(ProposalFull proposal, String openingDate, String closingDate, String address)
    {
        this.proposal = proposal;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.address = address;
    }
}
