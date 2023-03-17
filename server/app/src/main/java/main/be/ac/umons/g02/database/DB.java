package main.be.ac.umons.g02.database;


import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Effectue toutes les opérations à réaliser dans la base de données
 * Elle va en autre, gérer la connection et exécuter les requêtes,ect...
 */
public class DB
{
    private static String dataBaseName = "NAMEDB";
    private static String userName = "USERNAMEDB";
    private static String password = "PWDDB";
    private static String dbHost = "DBHOST"; //TODO added variable environment
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

    public static void setUserName(String userName)
    {
        DB.userName = userName;
    }

    public static void setPassword(String password)
    {
        DB.password = password;
    }

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
        //Class.forName("com.mysql.cj.jdbc.Driver"); //TODO useless and erase it
        connection = DriverManager.getConnection(
                "jdbc:mysql://"+dbHostV+":3306/" + dataBaseNameV,
                userNameV, passwordV);
    }

    public boolean executeQuery(String query, boolean returnData)
    {
        try
        {
            statement = connection.createStatement();
            if(returnData)
                resultSet = statement.executeQuery(query);
            else
                statement.executeUpdate(query);
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
            return false;
        }
        return true;

    }

    public ArrayList<ArrayList<String>> getResults(String[] column)
    {
        if(resultSet == null)
            return null;
        try
        {
            ArrayList<ArrayList<String>> array = new ArrayList<>(column.length);

            for(int i = 0; i < column.length; i++)
                array.add(new ArrayList<>());

            while(resultSet.next())
            {
                for(int i = 0; i < column.length; i++)
                    array.get(i).add(resultSet.getString(column[i]));

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

    boolean isThereSomething(String table, String[] attributes, String[] values)
    {
        if(attributes.length != values.length)
            throw new IllegalArgumentException("the size of the two arrays are not the same");
        if(attributes.length == 1)
            executeQuery("SELECT EXISTS(SELECT * FROM "+table+ " WHERE "+attributes[0]+"="+values[0]+") AS c", true);
        else
        {
            String query = "SELECT EXISTS(SELECT * FROM " +table+ " WHERE ";
            for(int i = 0; i < attributes.length; i++)
            {
                if(i == attributes.length - 1)
                    query += attributes[i] + "=" + values[i];
                query += attributes[i] + "=" + values[i]  + "AND ";
            }
            query += " ) AS c";
            executeQuery(query, true);
        }
        return getResults(new String[] {"c"}).get(0).get(0).equals("1");
    }
}
