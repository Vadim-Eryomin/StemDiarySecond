package ru.coistem.StemDiary;

import org.postgresql.util.PSQLException;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class Database {
    public static String connector = "jdbc:postgresql://ec2-54-247-103-43.eu-west-1.compute.amazonaws.com:5432/d4c70f7efoa6fc";
    public static String login = "hvrywehkzbxgro";
    public static String password = "1d549513c6fb0ff2d7f67f26acf1bc116182efd84805237635d4288a197e5c50";

//    public static String connector = "jdbc:postgresql://localhost:5432/stem";
//    public static String login = "postgres";
//    public static String password = "1";

    public static Connection connection;

    public static void connect() throws SQLException, IOException {
        connection = DriverManager.getConnection(connector, login, password);
    }

    public static ResultSet query(String query) throws Exception {
        query = query.toLowerCase();
        System.out.println(query);
        if (query.contains("return")) throw new Exception("null data");
        ResultSet set = null;
        try {
            Statement statement = connection.createStatement();
            if (query.contains("update") || query.contains("insert") || query.contains("delete"))
                statement.execute(query);
            else
                set = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e){
            System.out.println("null data in " + query);
        }
        return set;
    }
}
