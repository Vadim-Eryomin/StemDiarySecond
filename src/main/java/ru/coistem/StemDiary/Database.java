package ru.coistem.StemDiary;

import org.postgresql.util.PSQLException;

import java.sql.*;

public class Database {
    public static String connector = "jdbc:postgresql://localhost:5432/stem";
    public static String login = "postgres";
    public static String password = "1";
    public static Connection connection;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection(connector, login, password);
    }

    public static ResultSet query(String query) throws Exception {
        query = query.toLowerCase();
        System.out.println(query);
        if (query.split("return").length != 1) throw new Exception("null data");
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
