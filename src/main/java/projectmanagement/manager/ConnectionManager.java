package projectmanagement.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection con;

    private ConnectionManager() {
    }

    public static Connection getConnection(String db_url, String username, String pwd) {
        if (con != null)
            return con;
        try {
            con = DriverManager.getConnection(db_url, username, pwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}

