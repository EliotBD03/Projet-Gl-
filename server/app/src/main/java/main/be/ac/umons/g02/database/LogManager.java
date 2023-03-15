package main.be.ac.umons.g02.database;


import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;

public class LogManager
{

    public boolean doesAccountExist(String mail)
    {
        DB instance = DB.getInstance();
        instance.executeQuery("SELECT EXISTS(SELECT * FROM user WHERE mail='"+mail+"') AS 'mail'", true);
        return Integer.parseInt(instance.getResults(new String[] {"mail"}).get(0).get(0)) == 1;
    }

     public boolean isClient(String id)
     {
         DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM client WHERE client_id="+id+" ) AS r",true);
         ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"r"});
         System.out.println(results.get(0).get(0));
         return Integer.parseInt(results.get(0).get(0)) == 1;
     }

    public String checkAccount(String mail, String password)
    {
        if(doesAccountExist(mail))
        {
            DB.getInstance().executeQuery("SELECT password FROM user WHERE mail='"+ mail +"'", true);
            String savedPassword = DB.getInstance().getResults(new String[] {"password"}).get(0).get(0);
            if(BCrypt.checkpw(password, savedPassword))
            {
                DB.getInstance().executeQuery("SELECT id FROM user WHERE mail='"+mail+"'",true);
                return DB.getInstance().getResults(new String[] {"id"}).get(0).get(0);
            }
        }
        return null;
    }

    public String saveAccount(String mail, String password, boolean isClient, String name, String language) throws Exception
    {
        DB instance = DB.getInstance();

        if(doesAccountExist(mail))
            throw new Exception("The account already exists");

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

        return id;
    }

    public void deleteAccount(String id)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM client WHERE client_id="+id+") as c",true);
        boolean isClient = DB.getInstance().getResults(new String[]{"c"}).get(0).get(0).equals("1");

        if(isClient)
            DB.getInstance().executeQuery("DELETE FROM client WHERE client_id="+id, false);
        else
            DB.getInstance().executeQuery("DELETE FROM provider WHERE provider_id="+id, false);

        DB.getInstance().executeQuery("DELETE FROM language WHERE id="+id, false);
        DB.getInstance().executeQuery("DELETE FROM user WHERE id="+id,false);
    }

    public void changePassword(String id, String newPassword)
    {
        DB.getInstance().executeQuery("UPDATE user SET password='" + BCrypt.hashpw(newPassword, BCrypt.gensalt()) + "' WHERE id ='" + id+"'",false);
    }

    public String getName(String id)
    {
        DB.getInstance().executeQuery("SELECT name FROM user WHERE id="+id, true);
        return DB.getInstance().getResults(new String[] {"name"}).get(0).get(0);
    }
}
