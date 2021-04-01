package ua.edu.ukma.supermarket.configuration;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class JdbcConfiguration {

    private static final String DB_URL = "jdbc:h2:mem:supermarketDB";

    @SneakyThrows
    @Bean
    public Connection getConnection() {
        return DriverManager.getConnection(DB_URL, "sa", "");
    }

}
