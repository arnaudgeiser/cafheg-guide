package ch.hearc.cafheg.infrastructure.application;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.function.Supplier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ch.hearc.cafheg")
public class Application {

  private static DataSource dataSource;
  private static Connection connection;

  public static Connection getConnection() {
    return connection;
  }

  public static <T> T inTransaction(Supplier<T> inTransaction) {
    try {
      connection = dataSource.getConnection();
      return inTransaction.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @PostConstruct
  public void init() {
    System.out.println("Initializing datasource");
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:h2:mem:sample;INIT=RUNSCRIPT FROM 'src/main/resources/create.sql'");
    config.setMaximumPoolSize(1);
    dataSource = new HikariDataSource(config);
    System.out.println("Datasource initialized");
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
