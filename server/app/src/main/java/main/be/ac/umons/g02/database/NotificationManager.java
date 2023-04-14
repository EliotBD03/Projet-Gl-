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
        new Query("INSERT INTO notification(sender_id,receiver_id,linked_contract,context) " +
                "VALUES(" + senderId + "," + receiverId + ",'" + contractId + "','" + context + "')").executeWithoutResult();
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
        new Query("INSERT INTO notification(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context) " +
                "VALUES("+senderId+","+ receiverId +",'"+ proposalName+"',"+proposalOwnerId+",'"+context+"')").executeWithoutResult();
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
     * @return vrai si on a pu créer la notif(compteur libre), faux sinon
     */
    public boolean createNotification(String senderId, String receiverId, String proposalName, String proposalOwnerId, String context,  String ean, String address)
    {
        if(!new WalletManager().isTheCounterFree(ean))
            return false;

        new Query("INSERT INTO notification(sender_id, receiver_id, linked_proposal_name, provider_id_proposal, context, linked_ean, linked_address)"+
                " VALUES("+senderId+","+receiverId+",'"+proposalName+"',"+proposalOwnerId+",'"+context+"','"+ean+"','"+address+"')").executeWithoutResult();
        return true;
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
        String query = "SELECT * FROM notification WHERE receiver_id="+idUser + " ORDER BY creation_date DESC LIMIT "+ base+", "+limit;
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult
                (
                        "notification_id", "sender_id", "receiver_id", "linked_contract", "linked_proposal_name",
                        "provider_id_proposal", "context", "linked_ean", "linked_address", "creation_date"
                ).getTable();

        ArrayList<Notification> notifications = new ArrayList<>();
        for (ArrayList<String> row : table)
        {
            notifications.add(new Notification(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5),
                    row.get(6), row.get(7), row.get(8), row.get(9)));
        }
        int count = new Query("SELECT count(*) AS 'c' FROM notification WHERE receiver_id="+idUser).executeAndGetResult("c").getIntElem(0,0);
        return new Object[] {count, notifications};
    }

    /**
     * Crée un contrat en réponse à la validation de la notification (sens : client -> fournisseur).
     * (Used in the case of a client who sent a notification and the provider valid in response) //TODO proof
     * @param notificationId l'identifiant de la notification
     * @param ean le code ean
     * @param address l'adresse de la maison
     * @return vrai si on a pu créer la notif(compteur libre), faux sinon
     */
    public boolean acceptNotification(String notificationId, String ean, String address)
    {
        if(!new WalletManager().isTheCounterFree(ean))
            return false;

        ArrayList<String> row = new Query("SELECT * FROM notification WHERE notification_id="+notificationId)
                .executeAndGetResult
                        (
                                "sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal", "context"
                        )
                .getTable().get(0);

        new ContractManager().createContract(row.get(2), ean, row.get(3), address, row.get(1));

        createNotification(row.get(1), row.get(0), row.get(2), row.get(3),
                "Your contract was accepted by "+new LogManager().getName(row.get(1)));

        deleteNotification(notificationId);
        return true;
    }

    /**
    * Crée un contrat en réponse à la validation de la notification (sens : fournisseur -> client).
    * (Used in the case of a client who sent a notification and the provider valid in response) //TODO proof
    * @param notificationId l'identifiant de la notification
    */
    public void acceptNotification(String notificationId)
    {
        ArrayList<String> row = new Query("SELECT * FROM notification WHERE notification_id="+notificationId)
                .executeAndGetResult
                        (
                                "sender_id", "receiver_id", "linked_proposal_name", "provider_id_proposal",
                        "context", "linked_ean", "linked_address"
                        )
                .getTable().get(0);

        new ContractManager().createContract(row.get(2), row.get(5), row.get(3), row.get(6), row.get(0));
        createNotification(row.get(1), row.get(0), row.get(2), row.get(3),
                "Your contract was accepted by "+new LogManager().getName(row.get(1)));

        deleteNotification(notificationId);
    }

    /**
     * Refuse la demande de contrat.
     *
     * @param notificationId l'identifiant de la notification
     */
    public void refuseNotification(String notificationId)
    {
        ArrayList<String> row = new Query("SELECT * FROM notification WHERE notification_id="+notificationId).executeAndGetResult
                (
                        "sender_id","receiver_id","linked_proposal_name", "provider_id_proposal"
                ).getTable().get(0);

        String senderId = row.get(1);
        String receiverId = row.get(0);
        String linkedProposalName = row.get(2);
        String providerIdProposal = row.get(3);
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
        new Query("DELETE FROM notification WHERE notification_id="+idNotification).executeWithoutResult();
    }


}
