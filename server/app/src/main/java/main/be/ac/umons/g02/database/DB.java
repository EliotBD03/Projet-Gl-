package main.be.ac.umons.g02.database;

import org.checkerframework.checker.units.qual.A;

import java.sql.*;
import java.util.ArrayList;

/**
 * Effectue toutes les opérations à réaliser dans la base de données
 * Elle va en autre, gérer la connection et exécuter les requêtes,ect...
 */
public class DB
{
    private final String DATABASENAME = "babawallet_db";
    private final String USERNAME = "297895";
    private final String PASSWORD = "walletbaba_great";
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

    private void establishConnection() throws SQLException, ClassNotFoundException
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + DATABASENAME,
                USERNAME, PASSWORD);
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
}
