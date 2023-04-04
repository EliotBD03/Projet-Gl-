package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Notification;

import java.util.ArrayList;

public class NotificationManager
{
    /**
     * Crée une notification.
     *
     * @param senderId l'identifiant de l'envoyeur
     * @param receiverId l'identifiant de receveur
     * @param contractId l'identifiant de contrat
     * @param context le contexte
     */
    public void createNotification(String senderId, String receiverId, String contractId, String context)
    {
        DB.getInstance().executeQuery("INSERT INTO notification(sender_id,receiver_id,linked_contract,context) " +
                "VALUES(" + senderId + "," + receiverId + ",'" + contractId + "','" + context + "')",false);
    }

    /**
     * Crée une notification.
     *
     * @param senderId l'identifiant de l'envoyeur
     * @param receiverId l'identifiant du receveur
     * @param proposalName le nom de la proposition
     * @param proposalOwnerId l'identifiant de fournisseur possédant la proposition
     * @param context le contexte
     */
    public void createNotification(String senderId, String receiverId, String proposalName, String proposalOwnerId, String context)
    {
        DB.getInstance().executeQuery("INSERT INTO notification(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context) " +
                "VALUES("+senderId+","+ receiverId +",'"+ proposalName+"',"+proposalOwnerId+",'"+context+"')", false);
    }

    /**
     * Crée une notification.
     *
     * @param senderId l'identifiant de l'envoyeur
     * @param receiverId l'identifiant du receveur
     * @param proposalName le nom de la proposition
     * @param proposalOwnerId l'identifiant de fournisseur possédant la proposition
     * @param context le contexte
     * @param ean le code ean
     * @param address l'adresse
     */
    public void createNotification(String senderId, String receiverId, String proposalName, String proposalOwnerId, String context,  String ean, String address)
    {
        DB.getInstance().executeQuery("INSERT INTO notification(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context, linked_ean, linked_address)"+
                " VALUES("+senderId+","+receiverId+",'"+proposalName+"',"+proposalOwnerId+",'"+context+"','"+ean+"','"+address+"')",false);
    }

    /**
     * Donne toutes les notifications d'un certain utilisateur dans un intervalle : [base, base+limit]
     *
     * @param idUser l'identifiant de l'utilisateur
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return Un tableau contenant en premier indice le nombre total de notifications de l'utilisateur et une ArrayList de Notification en second.
     * @see Notification
     */
    public Object[] getAllNotifications(String idUser, int base, int limit)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE receiver_id="+idUser + " LIMIT "+ base+", "+limit, true);

        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(
                "notification_id", "sender_id", "receiver_id", "linked_contract", "linked_proposal_name", "provider_id_proposal", "context", "linked_ean", "linked_address", "creation_date");

        ArrayList<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < results.get(0).size(); i++)
        {
            notifications.add(new Notification(results.get(0).get(i), results.get(1).get(i), results.get(2).get(i), results.get(3).get(i), results.get(4).get(i), results.get(5).get(i), results.get(6).get(i), results.get(7).get(i), results.get(8).get(i), results.get(9).get(i)));
        }
        DB.getInstance().executeQuery("SELECT count(*) AS 'c' FROM notification WHERE receiver_id="+idUser, true);
        int count = Integer.parseInt(DB.getInstance().getResults("c").get(0).get(0));
        return new Object[] {count, notifications};
    }

    /**
     * Crée un contrat en réponse à la validation de la notification (sens : client -> fournisseur).
     * (Used in the case of a client who sent a notification and the provider valid in response) //TODO proof
     * @param notificationId l'identifiant de la notification
     * @param ean le code ean
     * @param address l'adresse de la maison
     */
    public void acceptNotification(String notificationId, String ean, String address)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id="+notificationId, true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults("sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal", "context");
        new ContractManager().createContract(results.get(2).get(0), ean, results.get(3).get(0), address, results.get(1).get(0));
        createNotification(results.get(1).get(0), results.get(0).get(0), results.get(2).get(0), results.get(3).get(0),
                "Your contract was accepted by "+new LogManager().getName(results.get(1).get(0)), ean,address);

    }

    /**
    * Crée un contrat en réponse à la validation de la notification (sens : fournisseur -> client).
    * (Used in the case of a client who sent a notification and the provider valid in response) //TODO proof
    * @param notificationId l'identifiant de la notification
    */
    public void acceptNotification(String notificationId)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id="+notificationId, true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults("sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal",
        "context", "linked_ean", "linked_address");
        new ContractManager().createContract(results.get(2).get(0), results.get(5).get(0), results.get(3).get(0), results.get(6).get(0), results.get(1).get(0));
        createNotification(results.get(1).get(0), results.get(0).get(0), results.get(2).get(0), results.get(3).get(0),
                "Your contract was accepted by "+new LogManager().getName(results.get(1).get(0)), results.get(5).get(0),results.get(6).get(0));
    }

    /**
     * Refuse la demande de contrat.
     *
     * @param notificationId l'identifiant de la notification
     */
    public void refuseNotification(String notificationId)
    {
        DB.getInstance().executeQuery("SELECT * FROM notification WHERE notification_id="+notificationId,true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults("sender_id","receiver_id","linked_proposal_name", "provider_id_proposal");
        String senderId = results.get(1).get(0);
        String receiverId = results.get(0).get(0);
        String linkedProposalName = results.get(2).get(0);
        String providerIdProposal = results.get(3).get(0);
        String context = "Your contract was denied by "+new LogManager().getName(senderId);
        deleteNotification(notificationId);
        createNotification(senderId, receiverId, linkedProposalName, providerIdProposal, context);
    }

    /**
     * Supprime la notification.
     *
     * @param idNotification l'identifiant de la notification
     */
    public void deleteNotification(String idNotification)
    {
        DB.getInstance().executeQuery("DELETE FROM notification WHERE notification_id="+idNotification, false);
    }


}
