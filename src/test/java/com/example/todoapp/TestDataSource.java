package com.example.todoapp;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class TestDataSource {
    private static JdbcDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
            dataSource.setUser("sa");
            dataSource.setPassword("sa");
        }
        return dataSource;
    }

    public static void closeDataSource(DataSource ds) throws SQLException {
        if (ds != null) {
            ds.unwrap(JdbcDataSource.class).getConnection().close();
        }
    }
}
