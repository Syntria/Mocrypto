package Mocrypto.Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnector {

    private Connection connect =null;

    // Creating database connection
    public Connection connectDB(){
        try {
            this.connect = DriverManager.getConnection(Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connect;
    }

    // Creating connection object. (Replace with singleton design pattern)
    public static Connection getInstance(){
        SQLConnector db= new SQLConnector();
        return db.connectDB();
    }
}
