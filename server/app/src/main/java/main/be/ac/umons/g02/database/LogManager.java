package main.be.ac.umons.g02.database;


import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;

public class LogManager
{

    private boolean doesAccountExist(String mail)
    {
        DB instance = DB.getInstance();
        instance.executeQuery("SELECT EXISTS(SELECT * FROM user WHERE mail='"+mail+"') AS 'mail'", true);
        return Integer.parseInt(instance.getResults(new String[] {"mail"}).get(0).get(0)) == 1;
    }

    public boolean checkAccount(String mail, String password)
    {
        DB.getInstance().executeQuery("SELECT password FROM user WHERE mail='"+ mail +"'", true);
        String savedPassword = DB.getInstance().getResults(new String[] {"password"}).get(0).get(0);
        System.out.println(savedPassword);
        System.out.println(BCrypt.hashpw(password, BCrypt.gensalt()));
        return BCrypt.checkpw(password, savedPassword);
    }

    public void saveAccount(String mail, String password, boolean isClient, String name, String language) throws Exception
    {
        DB instance = DB.getInstance();

        if(doesAccountExist(mail))
            throw new Exception("The account already exists"); //TODO make our own exception

        instance.executeQuery("SELECT EXISTS(SELECT * FROM user WHERE mail='"+mail+"') AS 'mail'", true);

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        instance.executeQuery("INSERT INTO user(mail,password,name) VALUES('"+ mail +"','"+ hashedPassword +"','"+ name +"')", false);

        instance.executeQuery("SELECT id FROM user WHERE mail='" + mail + "'", true);
        ArrayList<ArrayList<String>> results = instance.getResults(new String[] {"id"});

        String id = results.get(0).get(0);

        instance.executeQuery("INSERT INTO language(id,saved_language,favourite_language,current_language) VALUES(" + id + ",'" + language + "',1,1)", false);

        if(isClient)
            instance.executeQuery("INSERT INTO client(client_id) VALUES(" + id + ")", false);
        else
            instance.executeQuery("INSERT INTO provider(provider_id) VALUES(" + id + ")", false);
    }

    public void changePassword(String mail, String newPassword) throws Exception
    {
        if(doesAccountExist(mail))
            DB.getInstance().executeQuery("UPDATE user SET password='" + BCrypt.hashpw(newPassword, BCrypt.gensalt()) + "' WHERE mail ='" + mail+"'",false);
        else
            throw new Exception("the account doesn't exist");
    }
}
