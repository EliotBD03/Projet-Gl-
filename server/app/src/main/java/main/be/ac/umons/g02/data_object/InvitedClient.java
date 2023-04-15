package main.be.ac.umons.g02.data_object;

/** 
    *Classe permettant de regrouper les informations des clients invités sur un portefeuille
    *
    * @see WalletManager
    * @author Extension Claire
*/
public record InvitedClient(String permission, String invitedName, String invitedId, String invitedMail) {}
