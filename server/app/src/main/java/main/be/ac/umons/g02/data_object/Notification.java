package main.be.ac.umons.g02.data_object;

public class Notification
{
    private String id_notification;
    private String id_sender;
    private String nameSender;
    private String context;
    private String id_contract;

    public Notification(String id_notification, String id_sender, String nameSender, String context, String id_contract)
    {
        this.id_notification = id_notification;
        this.id_sender = id_sender;
        this.nameSender = nameSender;
        this.context = context;
        this.id_contract = id_contract;
    }

    public String getId_notification()
    {
        return id_notification;
    }

    public String getId_sender()
    {
        return id_sender;
    }

    public String getNameSender()
    {
        return nameSender;
    }

    public String getContext()
    {
        return context;
    }

    public String getId_contract()
    {
        return id_contract;
    }
}
