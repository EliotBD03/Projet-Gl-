package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Notification;

import java.util.ArrayList;

public class NotificationManager
{
    public void createNotification(String senderId, String receiverId, String contractId, String context)
    {
        DB.getInstance().executeQuery("INSERT INTO notification(sender_id,receiver_id,linked_contract,context) " +
                "VALUES ('" + senderId + "','" + receiverId + "','" + contractId + "','" + context + "')",false);
    }

    public ArrayList<Notification> getAllNotifications(String idUser)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE id="+idUser, true);

        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {
                "notification_id", "sender_id", "receiver_id", "linked_contract, context"});

        ArrayList<Notification> notifications = new ArrayList<>();
        for (ArrayList<String> result : results)
        {
            notifications.add(new Notification(result.get(0), result.get(1), result.get(2), result.get(3), result.get(4)));
        }

        return notifications;
    }

    public void acceptNotification(String idNotification)
    {
        //The fact of answering is only for contracts
        //TODO
    }

    public void refuseNotification(String idNotification)
    {
        //TODO
    }

    public void deleteNotification(String idNotification)
    {
        DB.getInstance().executeQuery("DELETE FROM notification WHERE id="+idNotification, false);
    }


}
