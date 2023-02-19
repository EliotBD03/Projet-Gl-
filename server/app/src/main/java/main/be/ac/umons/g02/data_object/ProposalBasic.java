package main.be.ac.umons.g02.data_object;

public class ProposalBasic
{
    protected String proposalName;
    protected String providerId;
    protected String nameProvider;
    protected TypeEnergy[] typeOfEnergy;
    protected Location[] location;

    public ProposalBasic(String nameProposal, String providerId, String nameProvider, TypeEnergy[] typeOfEnergy, Location[] location)
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

    public TypeEnergy[] getTypeOfEnergy()
    {
        return typeOfEnergy;
    }

    public Location[] getLocation()
    {
        return location;
    }
}
