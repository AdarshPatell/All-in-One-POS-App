package org.example.newchronopos.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String DB_PATH;

    static {
        String userHome = System.getProperty("user.home");
        String folderPath = userHome + File.separator + ".chronopos"; // hidden folder in home
        new File(folderPath).mkdirs(); // create if not exists
        DB_PATH = "jdbc:h2:" + folderPath + File.separator + "posdb;AUTO_SERVER=TRUE";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_PATH, "sa", "");
    }
}
