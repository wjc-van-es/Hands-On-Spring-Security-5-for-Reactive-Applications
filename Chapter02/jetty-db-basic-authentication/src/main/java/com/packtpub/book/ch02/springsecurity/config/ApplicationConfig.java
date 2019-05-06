package com.packtpub.book.ch02.springsecurity.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
//@PropertySource("classpath:mysqldb.properties")
@PropertySource("classpath:postgreSQLdb.properties")
public class ApplicationConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource getDataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("postgres.driver"));
        dataSource.setUrl(env.getProperty("postgres.jdbcUrl"));
        dataSource.setUsername(env.getProperty("postgres.username"));
        dataSource.setPassword(env.getProperty("postgres.password"));
        return dataSource;
    }
}
