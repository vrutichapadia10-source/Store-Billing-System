package Connection_Package;

import java.sql.*;

public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/grocery_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
