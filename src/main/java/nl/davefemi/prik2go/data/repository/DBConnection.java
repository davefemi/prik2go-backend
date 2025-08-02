package nl.davefemi.prik2go.data.repository;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.beans.factory.annotation.Value;
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
public class DBConnection {      
        private static final Logger logger = Logger.getLogger(DBConnection.class.getName());
        @Value("${spring.firebase.url}")
        private String DATABASE_URL;
        @Value("${spring.firebase.driver-class-name}")
        private String DRIVERNAME;
        @Value("${spring.firebase.username}")
        private String DATABASE_USER;
        @Value("${spring.firebase.password}")
        private String DATABASE_PASSWORD;

        /**
         * Methode die verbinding zoekt met de database en een Connection-object oplevert
         * als dit lukt.
         * @return Connection object.
         * @throws ApplicatieException als de driver niet gevonden wordt of er geen verbinding 
         * gemaakt kan worden met de database.
         */
        public Connection maakVerbinding() throws ApplicatieException {
                try {
                        Class.forName(DRIVERNAME);
                        logger.info("Maakt verbinding met database");
                        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
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
