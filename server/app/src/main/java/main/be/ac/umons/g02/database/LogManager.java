package main.be.ac.umons.g02.database;


import org.springframework.security.crypto.bcrypt.BCrypt;

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
        return new Query("SELECT EXISTS(SELECT * FROM user WHERE mail LIKE '"+mail+"') AS 'mail'").executeAndGetResult("mail").getIntElem(0,0)==1;
    }

    /**
     * Vérifie si l'utilisateur associé à un identifiant est cliente.
     *
     * @param id l'id de l'utilisateur
     * @return vrai si l'utilisateur est client, faux sinon
     */
     public boolean isClient(String id)
     {
         return new Query("SELECT EXISTS(SELECT * FROM client WHERE client_id="+id+" ) AS r").executeAndGetResult("r").getIntElem(0,0) == 1;
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
            String savedPassword = new Query("SELECT password FROM user WHERE mail='"+ mail +"'").executeAndGetResult("password").getStringElem(0,0);
            if(BCrypt.checkpw(password, savedPassword))
                return new Query("SELECT id FROM user WHERE mail='"+mail+"'").executeAndGetResult("id").getStringElem(0,0);
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

        if(doesAccountExist(mail))
            throw new Exception("The account already exists");

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        new Query("INSERT INTO user(mail,password,name) VALUES('"+ mail +"','"+ hashedPassword +"','"+ name +"')").executeWithoutResult();

        Table results =new Query("SELECT id FROM user WHERE mail='" + mail + "'").executeAndGetResult("id");

        String id = results.getStringElem(0,0);

        new Query("INSERT INTO language(id,saved_language,favourite_language,current_language) VALUES(" + id + ",'" + language + "',1,1)").executeWithoutResult();

        if(isClient)
            new Query("INSERT INTO client(client_id) VALUES(" + id + ")").executeWithoutResult();
        else
            new Query("INSERT INTO provider(provider_id) VALUES(" + id + ")").executeWithoutResult();

        return id;
    }

    /**
     * Supprime le compte associé à un identifiant.
     * PRECONDITION: on suppose que l'utilisateur n'a plus aucun contrat/proposition/portefeuille
     * @param id l'identifiant du compte à supprimer
     */

    public void deleteAccount(String id)
    {
        boolean isClient = new Query("SELECT EXISTS(SELECT * FROM client WHERE client_id="+id+") as c").executeAndGetResult("c").getIntElem(0,0) == 1;

        if(isClient)
            new Query("DELETE FROM client WHERE client_id="+id).executeWithoutResult();
        else
            new Query("DELETE FROM provider WHERE provider_id="+id).executeWithoutResult();

        new Query("DELETE FROM language WHERE id="+id).executeWithoutResult();
        new Query("DELETE FROM user WHERE id="+id).executeWithoutResult();
    }

    /**
     * Change le mot de passe actuelle associé à un utilisateur.
     *
     * @param mail le mail de l'utilisateur
     * @param newPassword le nouveau mot de passe
     */
    public void changePassword(String mail, String newPassword)
    {
        new Query("UPDATE user SET password='" + BCrypt.hashpw(newPassword, BCrypt.gensalt()) + "' WHERE mail ='" + mail +"'").executeWithoutResult();
    }

    /**
     * Donne le nom de l'utilisateur via son identifiant.
     *
     * @param id l'identifiant de l'utilisateur
     * @return le nom de l'utilisateur
     */
    public String getName(String id)
    {
        return new Query("SELECT name FROM user WHERE id="+id).executeAndGetResult("name").getStringElem(0,0);
    }
}
