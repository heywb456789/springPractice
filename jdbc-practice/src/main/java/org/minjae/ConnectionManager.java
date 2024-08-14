package org.minjae;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * packageName       : org.minjae
 * fileName         : ConnectionManager
 * author           : MinjaeKim
 * date             : 2024-08-14
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-14        MinjaeKim       최초 생성
 */
public class ConnectionManager {

    public static final String DB_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERID = "sa";
    public static final int MAX_POOL_SIZE = 40;
    public static final int MIN_IDLE = 5;

    private static final DataSource ds;

    static {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(DB_DRIVER);
        hikariDataSource.setJdbcUrl(DB_URL);
        hikariDataSource.setUsername(USERID);
        hikariDataSource.setPassword("");
        hikariDataSource.setMaximumPoolSize(MAX_POOL_SIZE);
        hikariDataSource.setMinimumIdle(MIN_IDLE);

        ds = hikariDataSource;
    }

//    public static DataSource getDataSource() {
//        HikariDataSource hikariDataSource = new HikariDataSource();
//        hikariDataSource.setDriverClassName(DB_DRIVER);
//        hikariDataSource.setJdbcUrl(DB_URL);
//        hikariDataSource.setUsername(USERID);
//        hikariDataSource.setPassword("");
//        hikariDataSource.setMaximumPoolSize(MAX_POOL_SIZE);
//        hikariDataSource.setMinimumIdle(MIN_IDLE);
//        return hikariDataSource;
//    }

    public static Connection getConnection() throws SQLException {
//        String url = "jdbc:h2:tcp://localhost/~/test";
//        String user = "sa";
//        String password = "";
//
//        try{
//            Class.forName("org.h2.Driver");
//            return DriverManager.getConnection(url, user, password);
//        }catch(Exception e){
//            return null;
//        }
//        return getDataSource().getConnection();
        try {
            return ds.getConnection();
        }catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static DataSource getDataSource() {
        return ds;
    }
}
