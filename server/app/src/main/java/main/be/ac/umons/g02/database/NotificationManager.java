package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Notification;

import java.util.ArrayList;

public class NotificationManager
{
    public void createNotification(String senderId, String receiverId, String contractId, String context)
    {
        DB.getInstance().executeQuery("INSERT INTO notification(sender_id,receiver_id,linked_contract,context) " +
                "VALUES(" + senderId + "," + receiverId + ",'" + contractId + "','" + context + "')",false);
    }

    public void createNotification(String senderId, String receiverId, String proposalName, String proposalOwnerId, String context)
    {
        DB.getInstance().executeQuery("INSERT INTO notification(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context) " +
                "VALUES("+senderId+","+ receiverId +",'"+ proposalName+"',"+proposalOwnerId+",'"+context+"')", false);
    }

    public void createNotification(String senderId, String receiverId, String proposalName, String proposalOwnerId, String context,  String ean, String address)
    {
        DB.getInstance().executeQuery("INSERT INTO notification(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context, ean, address)"+
                " VALUES("+senderId+","+receiverId+",'"+proposalName+"',"+proposalOwnerId+",'"+context+"','"+ean+"','"+address+"')",false);
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

    /**
     * Used in the case of a client who sent a notification and the provider valid in response
     * @param notificationId
     */
    public void acceptNotification(String notificationId, String ean, String address)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id="+notificationId, true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal",
                "context"});
        new ContractManager().createContract(results.get(2).get(0), ean, results.get(3).get(0), address, results.get(1).get(0));
        createNotification(results.get(0).get(0), results.get(1).get(0), results.get(2).get(0), results.get(3).get(0),
                "Your contract request was accepted by "+new LogManager().getName(results.get(1).get(0)), ean,address);

    }

    /**
     * Used in the case of a provider who send a notification and the client valid in response
     * @param notificationId
     */
    public void acceptNotification(String notificationId)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id="+notificationId, true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal",
        "context", "linked_ean", "linked_address"});
        new ContractManager().createContract(results.get(2).get(0), results.get(5).get(0), results.get(3).get(0), results.get(6).get(0), results.get(1).get(0));
        createNotification(results.get(0).get(0), results.get(1).get(0), results.get(2).get(0), results.get(3).get(0),
                "Your contract request was accepted by "+new LogManager().getName(results.get(1).get(0)), results.get(5).get(0),results.get(6).get(0));
    }

    public void refuseNotification(String notificationId)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id="+notificationId,true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[]
                {"sender_id","receiver_id","linked_proposal_name", "provider_id_proposal"});
        String senderId = results.get(1).get(0);
        String receiverId = results.get(0).get(0);
        String linkedProposalName = results.get(2).get(0);
        String providerIdProposal = results.get(3).get(0);
        String context = "Your contract request was denied by "+new LogManager().getName(receiverId);
        DB.getInstance().executeQuery("DELETE FROM notification WHERE notification_id="+notificationId,false);
        DB.getInstance().executeQuery("INSERT INTO notification VALUES(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context)"+
                " VALUES("+senderId+","+receiverId+",'"+linkedProposalName+"',"+providerIdProposal+",'"+context+"')",false);
    }

    public void deleteNotification(String idNotification)
    {
        DB.getInstance().executeQuery("DELETE FROM notification WHERE id="+idNotification, false);
    }


}
