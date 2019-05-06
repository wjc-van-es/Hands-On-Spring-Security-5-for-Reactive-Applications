package com.packtpub.book.ch02.springsecurity;

import com.packtpub.book.ch02.springsecurity.config.ApplicationConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This test class ends on IT so it will be skipped by the maven surefire plugin:
 * It cannot be tested on every environment currently only locally on willem-Latitude-5590
 * make sure the database is online
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class PostgresConnectionIT {

    @Autowired
    DataSource dataSource;

    private Connection connection01;

    @Before
    public void prepare() throws SQLException {
        assertNotNull(dataSource);
        connection01 = dataSource.getConnection();
        assertNotNull(connection01);
    }

    @After
    public void cleanUp() throws SQLException {
        connection01.close();
    }

    @Test
    public void testConnection() throws SQLException {

        PreparedStatement statement = connection01.prepareStatement(
                "select distinct us.username, au.authority from spring_security_schema.users us\n" +
                "inner join spring_security_schema.authorities au\n" +
                "on us.username = au.username");
        ResultSet resultSet = statement.executeQuery();
        assertNotNull(resultSet);
        Map<String, String> userMap = new HashMap<>();
        while(resultSet.next()){
            final String key = resultSet.getString(1);
            final String value = resultSet.getString(2);
            userMap.put(key, value);
        }
        assertEquals(2, userMap.size());
        assertEquals("ROLE_ADMIN", userMap.get("admin"));
        assertEquals("ROLE_USER", userMap.get("user"));

        resultSet.close();
        statement.close();
    }
}
