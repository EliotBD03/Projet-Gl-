package main.be.ac.umons.g02.data_object;

public class Notification
{
    private final String notificationId;
    private final String senderId;
    private final String receiverId;
    private final String contractId;
    private final String proposalName;
    private final String providerProposalId;
    private final String context;
    private final String ean;
    private final String address;
    private final String creationDate;


    public Notification(String notificationId, String senderId, String receiverId, String contractId, String proposalName, String providerProposalId, String context, String ean, String address,String creationDate)
    {
        this.notificationId = notificationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.contractId = contractId;
        this.proposalName = proposalName;
        this.providerProposalId = providerProposalId;
        this.context = context;
        this.ean = ean;
        this.address = address;
        this.creationDate = creationDate;
    }

    public String getNotificationId()
    {
        return notificationId;
    }

    public String getSenderId()
    {
        return senderId;
    }

    public String getReceiverId()
    {
        return receiverId;
    }

    public String getContractId()
    {
        return contractId;
    }
    public String getProposalName()
    {
        return proposalName;
    }

    public String getProviderProposalId()
    {
        return providerProposalId;
    }

    public String getContext()
    {
        return context;
    }
    public String getAddress(){return address;}
    public String getEan(){return ean;}
    public String getCreationDate(){return creationDate;}
}
