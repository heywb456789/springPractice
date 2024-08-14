package org.minjae;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

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

    public static DataSource getDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName("org.h2.Driver");
        hikariDataSource.setJdbcUrl("jdbc:h2:tcp://localhost/~/test");
        hikariDataSource.setUsername("sa");
        hikariDataSource.setPassword("");
        return hikariDataSource;
    }
}
