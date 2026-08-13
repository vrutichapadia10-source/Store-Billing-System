package Service_Package;

import Connection_Package.DBConnection;
import java.sql.Connection;

// Abstract parent class for all services
public abstract class BaseService {

    // Encapsulation: Protected method, accessible only to child classes
    protected Connection getConnection() throws Exception {
        return DBConnection.getConnection();
    }

    // Abstraction: Every service must describe itself
    public abstract void showServiceInfo();
}
