package proj.checkIN.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDBConnector {
    private static MariaDBConnector mdbc;

    private MariaDBConnector() {}

    public static MariaDBConnector getInstance() {
        if (mdbc == null) {
            mdbc = new MariaDBConnector();
        }
        return mdbc;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");
        String url = "jdbc:mariadb://**";
        Connection con = DriverManager.getConnection(url, "*", "*");

        return con;
    }
}
