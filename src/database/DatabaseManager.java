/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author AAGOOGLE
 */
public class DatabaseManager {

    private static final String HOST_NAME = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "todo_app";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/" + DATABASE;

    private static Connection connection;

    public static boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Connection openConnection() throws SQLException {
        if (isConnected()) {
            return connection;
        }
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        return connection;
    }

    public static boolean closeConnection() throws SQLException {
        if (isConnected()) {
            connection.close();
            connection = null;
            return true;
        }
        return false;
    }

    public static ResultSet querySQL(String query) throws SQLException {
        if (!isConnected()) {
            openConnection();
        }
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }

    public static Integer updateSQL(String query) throws SQLException {
        if (!isConnected()) {
            openConnection();
        }
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();

        ResultSet rs = preparedStatement.getGeneratedKeys();
        Integer lastInsertedId = -1;
        while (rs.next()) {
            lastInsertedId = rs.getInt(1);
        }
        return lastInsertedId;
    }

}
