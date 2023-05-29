package Mocrypto.Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnector {

    private Connection connect = null;
    private static SQLConnector instance = null; // Singleton instance

    // Private constructor to prevent direct instantiation
    private SQLConnector() {
    }

    // Creating database connection
    public Connection connectDB() {
        try {
            this.connect = DriverManager.getConnection(Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connect;
    }

    // Singleton getInstance() method to obtain the single instance
    public static Connection getInstance() {
        if (instance == null) {
            synchronized (SQLConnector.class) {
                if (instance == null) {
                    instance = new SQLConnector();
                }
            }
        }
        return instance.connectDB();
    }
}