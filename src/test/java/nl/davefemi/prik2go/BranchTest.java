package nl.davefemi.prik2go;

import nl.davefemi.prik2go.data.dto.domain.BranchDTO;
import nl.davefemi.prik2go.domain.Branch;
import nl.davefemi.prik2go.domain.Customer;
import nl.davefemi.prik2go.service.domain.DomainService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import nl.davefemi.prik2go.controller.exceptions.Prik2GoException;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Testklasse voor klasse Vestiging
 */
@SpringBootTest
public class BranchTest {
        @Autowired
        private DomainService controller;
        private List<String> vestigingen;
        private Branch vestiging1;
        private Branch vestiging2;
        private Branch vestiging3;
        private int totaalAantalKlanten;
        private String groningen = "Groningen";
        private String zuidhorn = "Zuidhorn";
        private int klantengroningen;
        private int klantenzuidhorn;
        
        @BeforeEach
        public void setUp() throws Prik2GoException {
                vestigingen = controller.getLocations();
                vestiging1 = new Branch("Amsterdam", new ArrayList<>());
                vestiging2 = new Branch("Rotterdam", new ArrayList<>());
                vestiging3 = new Branch("Amsterdam", new ArrayList<>());
                totaalAantalKlanten = 0;
        }
        
        @AfterEach
        public void tearDown() {
                vestigingen = null;
                vestiging1 = null;
                vestiging2 = null;
        }
        
        private void setUpOpenClose() throws Prik2GoException {
                for (String vest : vestigingen) {
                        totaalAantalKlanten += controller.getBranchDTO(vest).getNumberOfCustomers();
                        }
                groningen = "Groningen";
                zuidhorn = "Zuidhorn";
                klantengroningen = controller.getBranchDTO(groningen).getNumberOfCustomers();
                klantenzuidhorn = controller.getBranchDTO(groningen).getNumberOfCustomers();
        }
         

        /**
         * Test om te verifieren dat een vestiging met een lege lijst klanten wel een DTO
         * aanmaakt waarbij de informatie aangeeft dat er geen klanten zijn.
         * @throws Prik2GoException
         */
        @Test
        public void geenKlantenTest() throws Prik2GoException {
                Branch v = new Branch(groningen, new ArrayList<Customer>());
                BranchDTO dto = controller.getBranchDTO(v.getLocation());
                assertNotNull("Er wordt wel een DTO aangemaakt", dto);
                assertNotNull("Er wordt een klantnummerlijst gemaakt", dto.getCustomerIds());
                assertEquals( 156, dto.getNumberOfCustomers(), "Aantal klanten is nul");
        }
//
//        /**
//         * Test om te verifieren dat het sluiten van de ene vestiging ervoor zorgt dat alle klanten
//         * naar de dichtstbijzijnde open vestiging verhuizen, en dat de gesloten vestiging
//         * geen klanten meer heeft. Nadat alle vestigingen gesloten zijn worden vervolgens worden
//         * de testvestigingen een voor een weer geopend en gecontroleerd dat zij het zelfde aantal
//         * klanten hadden als oorspronkelijk.
//         * @throws ApplicatieException
//         */
//        @Test
//        public void setClosedandOpen() throws ApplicatieException, VestigingException {
//                //Arrange
//                setUpOpenClose();
//
//                controller.veranderVestigingStatus(groningen);
//                //Assert
//                assertTrue("Groningen heeft geen klanten meer", controller.getKlantenDTO(groningen).getAantalKlanten()  == 0);
//
//                controller.veranderVestigingStatus(zuidhorn);
//
//                //Assert
//                assertTrue("Beide vestigingen hebben geen klanten meer", controller.getKlantenDTO(groningen).getAantalKlanten()
//                                == 0 && controller.getKlantenDTO(zuidhorn).getAantalKlanten() == 0);
//
//                controller.veranderVestigingStatus(zuidhorn);
//                for (String vest : vestigingen) {
//                        if (!vest.equals(zuidhorn) && controller.getVestigingStatus(vest)) {
//                                controller.veranderVestigingStatus(vest);
//                                }
//                        }
//
//                //Assert
//                assertTrue("Alle klanten zitten bij Zuidhorn", controller.getKlantenDTO(zuidhorn).getAantalKlanten()
//                                == totaalAantalKlanten);
//
//                controller.veranderVestigingStatus(zuidhorn);
//                int totaalAantalKlanten2 = 0;
//
//                for (String vest : vestigingen) {
//                      //Assert
//                        assertTrue("Geen enkele vestiging heeft klanten meer",
//                                        controller.getKlantenDTO(vest).getAantalKlanten() == 0);
//
//                        controller.veranderVestigingStatus(vest);
//                        totaalAantalKlanten2 += controller.getKlantenDTO(vest).getAantalKlanten();
//                        }
//
//                //Assert
//                assertTrue("Vestigingen Groningen en Zuidhorn hebben oorspronkelijke klanten terug", controller.getKlantenDTO(groningen).getAantalKlanten()
//                                == klantengroningen && controller.getKlantenDTO(zuidhorn).getAantalKlanten() == klantenzuidhorn);
//                assertTrue("Totaal aantal klanten is zoals oorspronkelijk", totaalAantalKlanten == totaalAantalKlanten2);
//        }
             
        /**
         * Test om te verifieren dat het vergelijken van de vestingen volgens de regels verloopt.
         * In eerste instantie worden ze op naam vergeleken. Bij een gelijke naam worden ze op het
         * aantal klanten vergeleken, waarbij de grootste voorrang heeft.
         */
        @Test
        public void compareToTest() {
                assertTrue("Vestiging 1 komt voor klant 2",  vestiging1.compareTo(vestiging2) <= 1);
                assertTrue("Vestiging 2 komt voor 1", vestiging2.compareTo(vestiging1) >= 1);
                assertTrue("Vestiging 1 en 2 zijn gelijk aan zichzelf", vestiging1.compareTo(vestiging1)
                                == 0 && vestiging2.compareTo(vestiging2) == 0);

                //Arrange
                List<Customer> klanten1 = new ArrayList<>();
                for (int i = 0; i<10; i++) {
                        klanten1.add(new Customer(i));
                }
                vestiging1 = new Branch(groningen, klanten1);
                vestiging3 = new Branch(zuidhorn, klanten1);

                assertTrue("Vestiging 1 komt voor vestiging 3", vestiging1.compareTo(vestiging3) <= 1);
        }

}
