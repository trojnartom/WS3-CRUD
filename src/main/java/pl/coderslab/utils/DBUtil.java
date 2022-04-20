package pl.coderslab.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {

    public static final String SERVER_URL = "jdbc:mysql://localhost:3306";
    public static final String USER_NAME = "root";
    public static final String USER_PASSWORD = "coderslab";

    private static final String DELETE_QUERY = "DELETE FROM tableName where id = ?";

    private static DataSource dataSource;
        public static Connection getConnection() throws SQLException {
            return getInstance().getConnection();   }
        private static DataSource getInstance() {
            if (dataSource == null) {
                try {
                    Context initContext = new InitialContext();
                    Context envContext = (Context)initContext.lookup("java:/comp/env");
                    dataSource = (DataSource)envContext.lookup("jdbc/users");
                } catch (NamingException e) { e.printStackTrace(); }
            }
            return dataSource;
        }


    public static void insert(Connection conn, String query, String... params) {
        try ( PreparedStatement statement = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                statement.setString(i + 1, params[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printData(Connection conn, String query, String... columnNames) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                    if (columnNames.length == 0) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.println(resultSet.getString(i));
                        }
                    } else {
                        for (String columnName : columnNames) {
                            System.out.println(resultSet.getString(columnName));
                        }
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void remove(Connection conn, String tableName, int id) {
        try (PreparedStatement statement =
                     conn.prepareStatement(DELETE_QUERY.replace("tableName", tableName));) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}