package main.be.ac.umons.g02.database;


import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;

public class LogManager
{

    /**
     * Vérifie si l'utilisateur associé à un mail existe.
     *
     * @param mail le mail de l'utilisateur
     * @return vrai s'il est présent, faux sinon
     */
    public boolean doesAccountExist(String mail)
    {
        DB instance = DB.getInstance();
        instance.executeQuery("SELECT EXISTS(SELECT * FROM user WHERE mail LIKE '"+mail+"') AS 'mail'", true);
        return Integer.parseInt(instance.getResults("mail").get(0).get(0)) == 1;
    }

    /**
     * Vérifie si l'utilisateur associé à un identifiant est cliente.
     *
     * @param id l'id de l'utilisateur
     * @return vrai si l'utilisateur est client, faux sinon
     */
     public boolean isClient(String id)
     {
         DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM client WHERE client_id="+id+" ) AS r",true);
         ArrayList<ArrayList<String>> results = DB.getInstance().getResults("r");
         return Integer.parseInt(results.get(0).get(0)) == 1;
     }

    /**
     * Vérifie si le mail et le mot de passe correspondent bien à ce qui se trouve dans la base de données.
     *
     * @param mail le mail
     * @param password le mot de passe
     * @return L'identifiant utilisateur associé au mail et au mot de passe, null si les entrées sont incorrectes.
     */
    public String checkAccount(String mail, String password)
    {
        if(doesAccountExist(mail))
        {
            DB.getInstance().executeQuery("SELECT password FROM user WHERE mail='"+ mail +"'", true);
            String savedPassword = DB.getInstance().getResults("password").get(0).get(0);
            if(BCrypt.checkpw(password, savedPassword))
            {
                DB.getInstance().executeQuery("SELECT id FROM user WHERE mail='"+mail+"'",true);
                return DB.getInstance().getResults("id").get(0).get(0);
            }
        }
        return null;
    }

    /**
     * Enregistre un nouveau compte associé aux paramètres.
     *
     * @param mail le mail
     * @param password le mot de passe
     * @param isClient vrai si c'est le compte d'un client, faux sinon
     * @param name le nom
     * @param language la première langue de l'utilisateur
     * @return l'identifiant du nouvel utilisateur
     * @throws Exception Si le compte existe déjà.
     */
    public String saveAccount(String mail, String password, boolean isClient, String name, String language) throws Exception
    {
        DB instance = DB.getInstance();

        if(doesAccountExist(mail))
            throw new Exception("The account already exists");

        instance.executeQuery("SELECT EXISTS(SELECT * FROM user WHERE mail='"+mail+"') AS 'mail'", true);

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        instance.executeQuery("INSERT INTO user(mail,password,name) VALUES('"+ mail +"','"+ hashedPassword +"','"+ name +"')", false);

        instance.executeQuery("SELECT id FROM user WHERE mail='" + mail + "'", true);
        ArrayList<ArrayList<String>> results = instance.getResults("id");

        String id = results.get(0).get(0);

        instance.executeQuery("INSERT INTO language(id,saved_language,favourite_language,current_language) VALUES(" + id + ",'" + language + "',1,1)", false);

        if(isClient)
            instance.executeQuery("INSERT INTO client(client_id) VALUES(" + id + ")", false);
        else
            instance.executeQuery("INSERT INTO provider(provider_id) VALUES(" + id + ")", false);

        return id;
    }

    /**
     * Supprime le compte associé à un identifiant.
     * PRECONDITION: on suppose que l'utilisateur n'a plus aucun contrat/proposition/portefeuille
     * @param id l'identifiant du compte à supprimer
     */

    public void deleteAccount(String id)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM client WHERE client_id="+id+") as c",true);
        boolean isClient = DB.getInstance().getResults("c").get(0).get(0).equals("1");

        if(isClient)
            DB.getInstance().executeQuery("DELETE FROM client WHERE client_id="+id, false);
        else
            DB.getInstance().executeQuery("DELETE FROM provider WHERE provider_id="+id, false);

        DB.getInstance().executeQuery("DELETE FROM language WHERE id="+id, false);
        DB.getInstance().executeQuery("DELETE FROM user WHERE id="+id,false);
    }

    /**
     * Change le mot de passe actuelle associé à un utilisateur.
     *
     * @param mail le mail de l'utilisateur
     * @param newPassword le nouveau mot de passe
     */
    public void changePassword(String mail, String newPassword)
    {
        DB.getInstance().executeQuery("UPDATE user SET password='" + BCrypt.hashpw(newPassword, BCrypt.gensalt()) + "' WHERE mail ='" + mail +"'",false);
    }

    /**
     * Donne le nom de l'utilisateur via son identifiant.
     *
     * @param id l'identifiant de l'utilisateur
     * @return le nom de l'utilisateur
     */
    public String getName(String id)
    {
        DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+id, true);
        return DB.getInstance().getResults("name").get(0).get(0);
    }
}
