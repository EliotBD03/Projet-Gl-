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
                "notification_id", "sender_id", "receiver_id", "linked_contract, context"});

        ArrayList<Notification> notifications = new ArrayList<>();
        for (ArrayList<String> result : results)
        {
            notifications.add(new Notification(result.get(0), result.get(1), result.get(2), result.get(3), result.get(4)));
        }

        return notifications;
    }

    public void acceptNotification(String notificationId)
    {
        //The fact of answering is only for contracts
        //TODO
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
