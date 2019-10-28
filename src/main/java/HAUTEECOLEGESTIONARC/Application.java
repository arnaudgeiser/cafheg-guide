package HAUTEECOLEGESTIONARC;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class Application {

    private static DataSource dataSource;

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @PostConstruct
    public void init() {
        System.out.println("Initializing datasource");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:sample;INIT=RUNSCRIPT FROM 'src/main/resources/create.sql'");
        dataSource = new HikariDataSource(config);
        System.out.println("Datasource initialized");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
