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

    public static ResultSet query(String query) {
        ResultSet set = null;
        try {
            Statement statement = connection.createStatement();
            set = statement.executeQuery(query);
        } catch (PSQLException ignore) {
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return set;
    }
}
