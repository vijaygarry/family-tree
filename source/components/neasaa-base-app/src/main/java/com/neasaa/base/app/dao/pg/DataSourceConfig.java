package com.neasaa.base.app.dao.pg;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.utils.DBEncryptionUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class DataSourceConfig {
	
	@Bean (name = BeanNames.DATASOURCE_BEAN)
    public HikariDataSource dataSource() throws IOException {
        Properties props = new Properties();
        log.info("Loading db properties from file 'db.properties'");
        props.load(getClass().getClassLoader().getResourceAsStream("db.properties"));
        log.info("DB properties: " + props);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("app.datasource.url"));
        config.setUsername(props.getProperty("app.datasource.username"));
        String encryptedPwd = props.getProperty("app.datasource.password");
        config.setPassword(DBEncryptionUtil.decrypt(encryptedPwd));
        config.setDriverClassName(props.getProperty("app.datasource.driver-class-name"));

        // Optional HikariCP settings
        config.setPoolName(props.getProperty("app.datasource.pool.name", "HikariPool"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("app.datasource.pool.maximum-pool-size", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("app.datasource.pool.minimum-idle", "3")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("app.datasource.pool.idle-timeout", "30000")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("app.datasource.pool.connection-timeout", "20000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("app.datasource.pool.max-lifetime", "600000")));
        config.setAutoCommit(Boolean.parseBoolean(props.getProperty("app.datasource.pool.auto-commit", "false")));
        config.setConnectionTestQuery(props.getProperty("app.datasource.pool.connection-test-query", "SELECT 1"));       
        log.info("Loading datasource using config: " + config);
        return new HikariDataSource(config);
    }
	
	@Bean (name = BeanNames.JDBC_TEMPLATE_BEAN)
	public JdbcTemplate getJdbcTemplate (@Qualifier(BeanNames.DATASOURCE_BEAN) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	
	@Bean(name = BeanNames.TRANSACTION_MANAGER)
    public DataSourceTransactionManager transactionManager(
    		@Qualifier(BeanNames.DATASOURCE_BEAN) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
