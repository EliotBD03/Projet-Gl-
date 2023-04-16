package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Invitation;

import java.util.ArrayList;

public class InvitationManager
{  
/**
     * Crée une invitation.
     *
     * @param senderId l'identifiant de l'envoyeur
     * @param receiverId l'identifiant de receveur
     * @param address l'addresse du wallet
     * @param permission la permission du receveur sur le wallet
     * @param nameSender le nom de l'émetteur
     * @param type le type d'invitation
     * @return vrai si l'id du receveur entré existe bien et si l'utilisateur n'essaie pas de s'ajouter lui-même, faux sinon.
     * @author Extension Claire
     */
    public boolean createInvitation(String senderId, String receiverId, String address, String permission, String nameSender, String type)
    {
        if(new Query("SELECT EXISTS(SELECT * FROM user WHERE id='"+receiverId+"') AS c").executeAndGetResult("c").getIntElem(0,0) == 0 && senderId == receiverId){
                return false;
        }
        else{
            System.out.println("je suis une patate");
            new Query("INSERT INTO invitation(senderId,receiverId, address, permission, nameSender, type) " +
                    "VALUES(" + senderId + "," + receiverId + ",'" + address + "','" + permission + "','" + nameSender + "','"+ type +"')").executeWithoutResult();
            return true;
        }
    }

    /**
     * Refuse la demande d'invitation.
     *
     * @param invitationId l'identifiant de l'invitation
     * @author Extension Claire
     */
    public void refuseInvitation(String invitationId)
    {
        ArrayList<String> row = new Query("SELECT * FROM invitation WHERE invitationId="+invitationId).executeAndGetResult
                (
                        "senderId","receiverId","address", "permission"
                ).getTable().get(0);

        createInvitation(row.get(1), row.get(0), row.get(2), row.get(3), new LogManager().getName(row.get(1)), "denial");
        deleteInvitation(invitationId);
    }

    /**
     * Crée un invité en réponse à la validation de l'invitation.
     *
     * @param invitationId l'identifiant de l'invitation
     * @return vrai si le client est ajouté, faux si le client était déjà invité
     * @see InvitedClientManager
     * @author Extension Claire
     */
    public boolean acceptInvitation(String invitationId)
    {
        ArrayList<String> row = new Query("SELECT * FROM invitation WHERE invitationId="+invitationId)
                .executeAndGetResult
                        (
                                "senderId", "receiverId", "address", "permission"
                        )
                .getTable().get(0);

        boolean added = new InvitedClientManager().addInvited(row.get(0), row.get(2), row.get(1), row.get(3));
        deleteInvitation(invitationId);
        if(added){
            createInvitation(row.get(1), row.get(0), row.get(2), row.get(3), new LogManager().getName(row.get(1)), "accept");
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Donne toutes les invitations d'un certain client dans un intervalle : [base, base+limit]
     *
     * @param idUser l'identifiant de l'utilisateur
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return Un tableau contenant en premier indice le nombre total d'invitations de l'utilisateur et une ArrayList d'invitations en second.
     * @see Invitation
     * @author Extension Claire
     */
    public Object[] getAllInvitation(String idUser, int base, int limit)
    {
        String query = "SELECT * FROM invitation WHERE receiverId="+idUser + " LIMIT "+ base+", "+limit;
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult
                (
                        "invitationId", "senderId", "receiverId", "address", "permission",
                        "nameSender", "type"
                ).getTable();

        ArrayList<Invitation> invitations = new ArrayList<>();
        for (ArrayList<String> row : table)
        {
            invitations.add(new Invitation(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5), row.get(6)));
        }
        int count = new Query("SELECT count(*) AS 'c' FROM invitation WHERE receiverId="+idUser).executeAndGetResult("c").getIntElem(0,0);
        return new Object[] {count, invitations};
    }

    /**
     * Supprime l'invitation.
     *
     * @param idInvitation l'identifiant de l'invitation
     * @author Extension Claire
     */
    public void deleteInvitation(String idInvitation)
    {
        new Query("DELETE FROM invitation WHERE invitationId="+idInvitation).executeWithoutResult();
    }
}