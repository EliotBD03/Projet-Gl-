package main.be.ac.umons.g02.database;

import java.util.ArrayList;

public class LanguageManager
{
    private boolean doesTheLanguageNotExist(String userId, String language)
    {
        DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM language WHERE saved_language='"+language+"') AS 'result'", true);
        return Integer.parseInt(DB.getInstance().getResults(new String[]{"result"}).get(0).get(0)) != 1;
    }

    public String getLanguage(String userId)
    {
        DB.getInstance().executeQuery("SELECT saved_language FROM language WHERE current_language=1 AND id=" + userId, true);
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"saved_language"});

        if(results.get(0).size() == 0)
            return null;

        return results.get(0).get(0);
    }

    public String getFavouriteLanguage(String userId)
    {
        DB.getInstance().executeQuery("SELECT saved_language FROM language WHERE favourite_language=1 AND id=" + userId, true);
        return DB.getInstance().getResults(new String[] {"saved_language"}).get(0).get(0);
    }

    public ArrayList<String> getAllLanguages(String userId)
    {
        DB.getInstance().executeQuery("SELECT saved_language FROM language WHERE id=" + userId,true);
        return DB.getInstance().getResults(new String[] {"saved_language"}).get(0);
    }

    public void addLanguage(String userId, String language)
    {
        DB.getInstance().executeQuery("UPDATE language SET current_language=0 WHERE current_language=1",false);
        DB.getInstance().executeQuery("INSERT INTO language(id,saved_language,favourite_language,current_language) VALUES ("+userId+",'"+language+"',0,1)", false);
    }

    public void changeLanguage(String userId, String language)
    {
        if(doesTheLanguageNotExist(userId, language))
            addLanguage(userId, language);

        DB.getInstance().executeQuery("UPDATE language SET current_language=0 WHERE current_language=1", false);
        DB.getInstance().executeQuery("UPDATE language SET current_language=1 WHERE saved_language='"+ language +"'", false);
    }

    public void changeFavouriteLanguage(String userId, String language)
    {
        if(doesTheLanguageNotExist(userId, language))
            addLanguage(userId,language);
        
        DB.getInstance().executeQuery("UPDATE language SET favourite_language=0 WHERE favourite_language=1", false);
        DB.getInstance().executeQuery("UPDATE language SET favourite_language=1 WHERE saved_language='"+language+"'",false);
    }
}
