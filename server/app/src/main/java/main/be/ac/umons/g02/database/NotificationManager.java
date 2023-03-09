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

    public void createNotification(String senderId, String receiverId, String proposalName, String proposalOwnerId, String context)
    {
        DB.getInstance().executeQuery("INSERT INTO notification(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context) " +
                "VALUES('"+senderId+","+ receiverId +",'"+ proposalName+"',"+proposalOwnerId+",'"+context+"')", false);
    }

    public ArrayList<Notification> getAllNotifications(String idUser, int base, int limit)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE id="+idUser + " LIMIT "+ base+", "+(base+limit), true);

        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {
                "notification_id", "sender_id", "receiver_id", "linked_contract", "context", "creation_date"});

        ArrayList<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < results.get(0).size(); i++)
        {
            notifications.add(new Notification(results.get(0).get(i), results.get(1).get(i), results.get(2).get(i), results.get(3).get(i), results.get(4).get(i), results.get(5).get(i)));
        }

        return notifications;
    }

    public void acceptNotification(String notificationId, String ean, String address)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification", true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal"});

    }

    public void refuseNotification(String notificationId)
    {
        //TODO delete notif and notice the sender
        String context = "Votre demande de contrat a été refusé.";
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id="+notificationId,true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[]
                {"sender_id","receiver_id","linked_proposal_name", "provider_id_proposal"});
        String senderId = results.get(1).get(0);
        String receiverId = results.get(0).get(0);
        String linkedProposalName = results.get(2).get(0);
        String providerIdProposal = results.get(3).get(0);
        DB.getInstance().executeQuery("DELETE FROM notification WHERE notification_id="+notificationId,false);
        DB.getInstance().executeQuery("INSERT INTO notification VALUES(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context)"+
                " VALUES("+senderId+","+receiverId+",'"+linkedProposalName+"',"+providerIdProposal+",'"+context+"')",false);
    }

    public void deleteNotification(String idNotification)
    {
        DB.getInstance().executeQuery("DELETE FROM notification WHERE id="+idNotification, false);
    }


}
