package de.telekom.todomanager.springboot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    @Bean
    public DataSource getDataSource() {
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");

            logger.debug("Try to connect to the repositories.");

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://freespeech-online.ddnss.de:3306/todomanager_Online?serverTimezone=gmt");
            dataSource.setUsername("rene");
            dataSource.setPassword("rene");

            logger.info("Verbindung zur Datenbank erfolgreich hergestellt.");
            logger.info(dataSource.toString());

            return dataSource;

//            return new SingleConnectionDataSource(DriverManager.getConnection("jdbc:mysql://localhost/todomanager?serverTimezone=UTC&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false", "root", "1997"), true);

        } catch (Exception e) {
            logger.error(e.getStackTrace().toString());
        }
        return null;
    }
}
