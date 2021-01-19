package com.xiaohu.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


//多数据源
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
@Slf4j
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /* 但数据源 spring
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        showConnection();
        showData();
    }

    private void showConnection() throws SQLException {
        log.info(dataSource.toString());
        Connection conn = dataSource.getConnection();
        log.info(conn.toString());
        conn.close();
    }

    private void showData() {
        jdbcTemplate.queryForList("SELECT * FROM FOO")
                .forEach(row -> log.info(row.toString()));
    }*/



    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.spring")
    public DataSourceProperties springDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource springDataSource() {
        DataSourceProperties dataSourceProperties = springDataSourceProperties();
        log.info("spring datasource: {}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Resource
    public PlatformTransactionManager springTxManager(DataSource springDataSource) {
        return new DataSourceTransactionManager(springDataSource);
    }

    @Autowired
    //@Qualifier("jdbcTemplateSpring")
    private JdbcTemplate jdbcTemplateSpring;

    @Bean
    JdbcTemplate jdbcTemplateSpring(DataSource springDataSource) {
        return new JdbcTemplate(springDataSource);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.world")
    public DataSourceProperties worldDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource worldDataSource() {
        DataSourceProperties dataSourceProperties = worldDataSourceProperties();
        log.info("world datasource: {}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Resource
    public PlatformTransactionManager worldTxManager(DataSource worldDataSource) {
        return new DataSourceTransactionManager(worldDataSource);
    }

    @Autowired
    //@Qualifier("jdbcTemplateWorld")
    private JdbcTemplate jdbcTemplateWorld;

    @Bean
    JdbcTemplate jdbcTemplateWorld(DataSource worldDataSource) {
        return new JdbcTemplate(worldDataSource);
    }

    @Override
    public void run(String... args) throws Exception {
        jdbcTemplateSpring.queryForList("SELECT * FROM FOO").forEach(row -> log.info(row.toString()));
        jdbcTemplateWorld.queryForList("SELECT * FROM `country` ").forEach(row -> log.info(row.toString()));
    }
}
