package main.be.ac.umons.g02.data_object;

public class Notification
{
    private final String id_notification;
    private final String senderId;
    private final String receiverId;
    private final String contractId;
    private final String context;

    public Notification(String id_notification, String sender_id, String receiverId, String contractId, String context)
    {
        this.id_notification = id_notification;
        this.senderId = sender_id;
        this.receiverId = receiverId;
        this.contractId = contractId;
        this.context = context;
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
}
