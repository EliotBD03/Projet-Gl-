package main.be.ac.umons.g02.data_object;

public class Notification
{
    private final String id_notification;
    private final String senderId;
    private final String receiverId;
    private final String contractId;
    private final String context;

    private final String creationDate;

    public Notification(String id_notification, String sender_id, String receiverId, String contractId, String context, String creationDate)
    {
        this.id_notification = id_notification;
        this.senderId = sender_id;
        this.receiverId = receiverId;
        this.contractId = contractId;
        this.context = context;
        this.creationDate = creationDate;
    }

    public String getId_notification()
    {
        return id_notification;
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

    public String getContext()
    {
        return context;
    }
    public String getCreationDate(){return creationDate;}
}
