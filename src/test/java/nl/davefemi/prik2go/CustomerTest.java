package nl.davefemi.prik2go;

import static org.junit.Assert.*;

import nl.davefemi.prik2go.domain.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import nl.davefemi.prik2go.exceptions.Prik2GoException;

/**
 * Testklasse voor klasse Klant
 */
@SpringBootTest
public class CustomerTest  {
        private Customer klant1;
        private Customer klant2;
        
        @BeforeEach
        public void setUp() throws Prik2GoException {

                klant1 = new Customer(20);
                klant2 = new Customer(30);
        }
        
        @AfterEach
        public void tearDown() {
                klant1 = null;
                klant2 = null;
        }
        
        /**
         * Test om te verifieren dat de compare methode van de klasse werkt
         * naar behoren. Klant moet worden vergeleken aan de hand van het klant
         * nummer.
         */
        @Test
        public void compareToTest() {
                assertEquals("Klant 1 is kleiner dan klant 2", -1, klant1.compareTo(klant2));
                assertEquals("Klant 2 is groter dan klant 1", 1, klant2.compareTo(klant1));
                assertEquals("Klant 1 en 2 zijn gelijk aan zichzelf", 0, klant1.compareTo(klant1) + klant2.compareTo(klant2));
        }
}
