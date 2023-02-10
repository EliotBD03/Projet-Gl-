package main.be.ac.umons.g02.database;

import java.util.ArrayList;

public class CommonDB
{
    /**
     * vérifie si le compte existe
     * @param mail
     * @param pwd mdp
     * @param isClient s'il est client oui, sinon fournisseur
     * @return vrai s'il est présent, faux sinon
     */
    public boolean checkAccount(String mail, String pwd, boolean isClient)
    {
        //TODO
        return false;
    }

    /**
     * crée un compte (s'il n'existe pas)
     */

    public void saveAccount(String mail, String pwd, boolean isClient, String name, String language)
    {

    }

    /**
     * change le mot de passe existant en un nouveau
     */
    public void changePwd(String id_user, String newPwd)
    {

    }

    /**
     * retourne la langue actuelle de l'utilisateur
     */

    public String getLanguage(String id_user)
    {
        //TODO
        return null;
    }

    /**
     * retourne la langue préférée de l'utilisateur
     */
    public String getFavouriteLanguage(String id_user)
    {
        //TODO
        return null;
    }

    /**
     * retourne toutes les langues que l'utilisateur possède
     * @param id_user
     * @return
     */
    public ArrayList<String> getAllLanguages(String id_user)
    {
        //TODO
        return null;
    }




}
