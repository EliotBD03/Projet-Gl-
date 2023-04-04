package main.be.ac.umons.g02.database;

import java.util.ArrayList;

public class LanguageManager
{
    /**
     * Vérifie si la langue existe pour un utilisateur donné.
     *
     * @param userId l'id de l'utilisateur
     * @param language le nom de la langue
     * @return Vrai si elle existe, faux sinon.
     */
    private boolean doesTheLanguageNotExist(String userId, String language)
    {
        return new Query("SELECT EXISTS(SELECT * FROM language WHERE saved_language='"+language+
                "' AND id="+userId+") AS 'result'").executeAndGetResult("result").getIntElem(0,0) != 1;
    }

    /**
     * Donne la langue actuellement utilisée par un utilisateur donné.
     *
     * @param userId l'id de l'utilisateur
     * @return le nom de la langue (null si aucune langue associée)
     */
    public String getCurrentLanguage(String userId)
    {
        DB.getInstance().executeQuery("SELECT saved_language FROM language WHERE current_language=1 AND id=" + userId, true);
        Table table = new Query("SELECT saved_language " +
                "FROM language " +
                "WHERE current_language=1 " +
                "AND id=" + userId).executeAndGetResult("saved_language");

        if(table.getTable().size() == 0)
            return null;

        return table.getStringElem(0,0);
    }

    /**
     * Donne la langue préférée d'un utilisateur.
     *
     * @param userId l'id de l'utilisateur
     * @return le nom de la langue
     */
    public String getFavouriteLanguage(String userId)
    {
        return new Query("SELECT saved_language " +
                "FROM language " +
                "WHERE favourite_language=1 " +
                "AND id=" + userId).executeAndGetResult("saved_language").getStringElem(0,0);
    }

    /**
     * Renvoie toutes les langues d'un utilisateur dans un intervalle : [base, base + limit]
     *
     * @param userId l'id de l'utilisateur
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return Un tableau contenant en premier indice le nombre total de langues de l'utilisateur et une Arraylist contenant les noms des langues dans l'intervalle.
     */
    public Object[] getAllLanguages(String userId, int base, int limit)
    {

        ArrayList<String> languages = new Query("SELECT saved_language " +
                "FROM language " +
                "WHERE id=" + userId + " " +
                "LIMIT "+base+", "+limit).executeAndGetResult("saved_language").getColumn(0);
        int count = new Query("SELECT count(*) AS 'c' FROM language WHERE id="+userId).executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count, languages};
    }

    /**
     * Rajoute une langue pour un utilisateur donné.
     *
     * @param userId l'identifiant de l'utilisateur
     * @param language le nom de la langue
     */
    public void addLanguage(String userId, String language)
    {
        new Query("UPDATE language SET current_language=0 WHERE current_language=1").executeWithoutResult();
        new Query("INSERT INTO language(id,saved_language,favourite_language,current_language) VALUES ("+userId+",'"+language+"',0,1)").executeWithoutResult();
    }

    /**
     * Change la langue actuelle d'un utilisateur
     *
     * @param userId l'identifiant de l'utilisateur
     * @param newLanguage le nom de la langue
     */
    public void changeCurrentLanguage(String userId, String newLanguage)
    {
        if(doesTheLanguageNotExist(userId, newLanguage))
            addLanguage(userId, newLanguage);

        new Query("UPDATE language SET current_language=0 WHERE current_language=1").executeWithoutResult();
        new Query("UPDATE language SET current_language=1 WHERE saved_language='"+ newLanguage +"'").executeWithoutResult();
    }

    /**
     * Change la langue préférée d'un utilisateur donné.
     *
     * @param userId l'id de l'utilisateur
     * @param language le nom de langue qui va remplacer la précédente.
     */
    public void changeFavouriteLanguage(String userId, String language)
    {
        if(doesTheLanguageNotExist(userId, language))
            addLanguage(userId,language);

        new Query("UPDATE language SET favourite_language=0 WHERE favourite_language=1").executeWithoutResult();
        new Query("UPDATE language SET favourite_language=1 WHERE saved_language='"+language+"'").executeWithoutResult();
    }
}
