package org.goldgame.utilities;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class SqlSetup {

    private SqlSetup() {
        throw new IllegalStateException("Utility class");
    }

    private static final String SCRIPT = ";INIT=RUNSCRIPT FROM 'classpath:/script.sql'";
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:./db/gold_game;DB_CLOSE_DELAY=-1" + SCRIPT; // inMemory
    //private static final String DB_URL = "jdbc:h2:file:./db/gold_game" + SCRIPT; // embedded
    private static final String USER = "test";
    private static final String PASS = "test";

    public static void loadDriver() {
        log.info("Registering JDBC drive: {}", JDBC_DRIVER);
        DbUtils.loadDriver(JDBC_DRIVER);
    }

    public static Connection createConnection() throws SQLException {
        log.info("Connecting to database...");
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        log.info("Connection successful..");
        return conn;
    }

    public static void logSqlError(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                log.error("SQLState: " + ((SQLException) e).getSQLState());
                log.error(("Error Code: " + ((SQLException) e).getErrorCode()));
                log.error(("Message: " + e.getMessage()));
                log.error("StackTrace: {}", (Object) e.getStackTrace());
                Throwable t = ex.getCause();
                while (t != null) {
                    log.error(("Cause: " + t));
                    t = t.getCause();
                }
            }
        }
    }
}
