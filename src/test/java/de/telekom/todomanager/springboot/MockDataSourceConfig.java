package de.telekom.todomanager.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@TestComponent
public class MockDataSourceConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Overwrite the real oracle datasource with this in-memory version so tests can run independently
     * @return
     */
    @Bean
    @Primary
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");

        logger.info("############################## H MOCK DS");
        return dataSource;
    }

}
