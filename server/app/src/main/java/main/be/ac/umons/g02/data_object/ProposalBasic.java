package main.be.ac.umons.g02.data_object;

public class ProposalBasic
{
    protected String nameProposal;
    protected String id_provider;
    protected String nameProvider;
    protected TypeEnergy typeOfEnergy;
    protected String location;

    ProposalBasic(String nameProposal, String id_provider, String nameProvider, TypeEnergy typeOfEnergy, String location)
    {
        this.nameProposal = nameProposal;
        this.id_provider = id_provider;
        this.nameProvider = nameProvider;
        this.typeOfEnergy = typeOfEnergy;
        this.location = location;
    }
}
