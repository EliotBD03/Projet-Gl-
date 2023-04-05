package main.be.ac.umons.g02.database;


import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Effectue toutes les opérations à réaliser dans la base de données.
 * Elle va en autre, gérer la connection et exécuter les requêtes,ect...
 */
public class DB
{
    private static String dataBaseName = "NAMEDB";
    private static String userName = "USERNAMEDB";
    private static String password = "PWDDB";
    private static String dbHost = "DBHOST";
    private static final Map<String, String> env = System.getenv();
    private static DB instance;
    private Connection connection;
    private ResultSet resultSet;
    private Statement statement;
    private DB()
    {
        try
        {
            establishConnection();
            instance = this;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            instance = null;
        }
    }

    public static DB getInstance()
    {
        if(instance == null)
            new DB();
        return instance;
    }

    public static void setDataBaseName(String dataBaseName)
    {
        DB.dataBaseName = dataBaseName;
    }

    /**
     * Etablis la connection avec la base de données.
     *
     * @throws SQLException Si la connection est impossible.
     * @throws ClassNotFoundException Si le driver requis ne se trouve pas sur la machine.
     */
    private void establishConnection() throws SQLException, ClassNotFoundException
    {
        String dataBaseNameV = null;
        String userNameV = null;
        String passwordV = null;
        String dbHostV = null;

        if(env.containsKey(dataBaseName) && env.containsKey(userName) && env.containsKey(password) && env.containsKey(dbHost))
        {
            dataBaseNameV = env.get(dataBaseName);
            userNameV = env.get(userName);
            passwordV = env.get(password);
            dbHostV = env.get(dbHost);
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + dbHostV + "/" + dataBaseNameV, userNameV, passwordV);
    }

    /**
     * Exécute la requête donnée en paramètre.
     *
     * @param query une requête en MySQL/MariaDB
     * @param returnData Possibilité par la suite de recevoir de l'information provenant de la requête si le booléen est mis à vrai.
     * @return vrai si la requête a été correctement exécutée.
     */
    public boolean executeQuery(String query, boolean returnData)
    {
        try
        {
            statement = connection.createStatement();
            if(returnData)
            {
                resultSet = statement.executeQuery(query);
                if(resultSet == null) //Ceci s'obtient dans le cas possible d'une déconnexion inattendue avec la BDD
                {
                    establishConnection();
                    resultSet = statement.executeQuery(query);
                }
            }
            else
                statement.executeUpdate(query);
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;

    }

    /**
     * Donne le résultat d'une requête exécutée précédemment.
     * PRECONDITION: la méthode executeQuery a été appelée est returnData à Vrai
     *
     * @param columns les colonnes
     * @return ArrayList d'ArrayList → matrice dont les colonnes pour les lignes et les valeurs pour les colonnes ╭(ʘ̆~◞౪◟~ʘ̆)╮
     */
    public ArrayList<ArrayList<String>> getResults(String... columns)
    {
        if(resultSet == null)
            return null;
        try
        {
            ArrayList<ArrayList<String>> array = new ArrayList<>(columns.length);

            for(int i = 0; i < columns.length; i++)
                array.add(new ArrayList<>());

            while(resultSet.next())
            {
                for(int i = 0; i < columns.length; i++)
                    array.get(i).add(resultSet.getString(columns[i]));

            }
            statement.close();
            return array;
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Envoie une requête pour garder une connection persistante avec le serveur.
     */
    public void refreshTimeOut()
    {
        executeQuery("SELECT 1",false);
    }
}
