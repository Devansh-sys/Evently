package eventlyv1.DBUtility;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static String dbUrl;
    private static String user;
    private static String pass;


    public static void init(String url, String dbUser, String dbPass) {
        dbUrl = url;
        user = dbUser;
        pass = dbPass;
        System.out.println("DBConnection initialized with URL/account.");
    }


    public static Connection getConnection() throws SQLException {
        if (dbUrl == null || user == null || pass == null) {
            throw new SQLException("Database connection parameters not initialized.");
        }
        return DriverManager.getConnection(dbUrl, user, pass);
    }
}
