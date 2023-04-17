package main.be.ac.umons.g02.data_object;

public class ProposalBasic
{
    protected String proposalName;
    protected String providerId;
    protected String nameProvider;
    protected String typeOfEnergy;
    /*
    location est une séquence de 3 bits :
        le premier bit : la région Wallone
        le second bit : la région flamande
        le troisième bit : la région Bruxelles-Capitale
    */
    protected String location;


    public ProposalBasic(String nameProposal, String providerId, String nameProvider, String typeOfEnergy, String location)
    {
        this.proposalName = nameProposal;
        this.providerId = providerId;
        this.nameProvider = nameProvider;
        this.typeOfEnergy = typeOfEnergy;
        this.location = location;
    }

    public String getProposalName()
    {
        return proposalName;
    }

    public String getProviderId()
    {
        return providerId;
    }

    public String getNameProvider()
    {
        return nameProvider;
    }

    public String getTypeOfEnergy()
    {
        return typeOfEnergy;
    }

    public String getLocation()
    {
        return location;
    }
}
