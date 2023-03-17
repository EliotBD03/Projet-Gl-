//implementation group: 'mysql', name: 'mysql-connector-java', version: '8.0.31'
package bddtest;

import java.sql.*;

public class App 
{
    public static void establishConnection() throws SQLException, ClassNotFoundException
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://mysql-babawallet.alwaysdata.net/babawallet_db", "297895", "walletbaba_great");
        System.out.println("coucou");
        //mysql -h mysql-babawallet.alwaysdata.net -u 297895 -p babawallet_db
    }

    public static void main(String[] args) throws Exception
    {
        establishConnection();
    }
}
