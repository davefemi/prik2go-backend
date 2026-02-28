package nl.davefemi.prik2go;

import static org.junit.Assert.*;
import java.sql.Connection;
import org.junit.Test;
import nl.davefemi.prik2go.data.repository.DBConnection;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testklasse voor DBConnectionTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DBConnectionTest {
        @Autowired
        private DBConnection dbConnection;

        /**
         * Test controleert of er verbinding wordt gemaakt met de database
         * @throws ApplicatieException
         */
        @Test
        public  void maakVerbindingMetDatabaseTest() throws ApplicatieException {
                Connection connection = dbConnection.maakVerbinding();
                assertTrue("Er is verbinding gemaakt met de database", connection != null);
        }
}
