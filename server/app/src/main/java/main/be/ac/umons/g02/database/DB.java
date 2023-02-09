package main.be.ac.umons.g02.database;

import java.sql.*;

/**
 * Effectue toutes les opérations à réaliser dans la base de données
 * Elle va en autre, gérer la connection et exécuter les requêtes,ect...
 */
public class DB
{
    private final String DATABASENAME = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("babawallet_db.db"));
    private final String USERNAME = "297895";
    private final String PASSWORD = "walletbaba_great";
    private static DB instance;
    private Connection connection;
    private boolean isConnectionEstablished;
    private ResultSet resultSet;
    private DB()
    {
        isConnectionEstablished = establishConnection();
        instance = this;
    }

    public static DB getInstance()
    {
        if(instance == null)
            new DB();
        return instance;
    }

    private boolean establishConnection() {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + DATABASENAME,
                    USERNAME, PASSWORD);

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage()); //pour les tests unitaires
            return false;
        }
        return true;
    }

    public boolean isConnectionEstablished()
    {
        return isConnectionEstablished;
    }

    public boolean executeQuery(String query)
    {
        try
        {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            statement.close();
        }
        catch (SQLException e)
        {
            return false;
        }
        return true;
    }
}
