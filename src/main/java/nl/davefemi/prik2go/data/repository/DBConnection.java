package nl.davefemi.prik2go.data.repository;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Klasse die verantwoordelijk is voor het maken van een verbinding met de database.
 */
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "external.firebase")
public class DBConnection {
        private static final Logger logger = Logger.getLogger(DBConnection.class.getName());
        @Value("${spring.datasource.url}")
        private String database_url;
        @Value("${spring.datasource.driver-class-name}")
        private String drivername;
        @Value("${spring.datasource.username}")
        private String database_user;
        @Value("${spring.datasource.password}")
        private String database_password;

        /**
         * Methode die verbinding zoekt met de database en een Connection-object oplevert
         * als dit lukt.
         * @return Connection object.
         * @throws ApplicatieException als de driver niet gevonden wordt of er geen verbinding 
         * gemaakt kan worden met de database.
         */
        public Connection maakVerbinding() throws ApplicatieException {
                try {
                        Class.forName(drivername);
                        logger.info("Maakt verbinding met database");
                        return DriverManager.getConnection(database_url, database_user, database_password);
                }
                catch (ClassNotFoundException e) {
                        logger.warning(e.getMessage());
                        throw new ApplicatieException ("Databasedriver is niet geladen.");
                }
                catch (SQLException e) {
                        logger.warning(e.getMessage());
                        throw new ApplicatieException ("Verbinding maken met database is mislukt.");  
                }
        }
}
