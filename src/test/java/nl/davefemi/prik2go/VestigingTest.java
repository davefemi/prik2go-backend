package nl.davefemi.prik2go;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import nl.davefemi.prik2go.service.domain.DomainServiceOld;
import nl.davefemi.prik2go.domain.Vestiging;
import nl.davefemi.prik2go.exceptions.ApplicatieException;

/**
 * Testklasse voor klasse Vestiging
 */
@SpringBootTest
public class VestigingTest  {
        @Autowired
        private DomainServiceOld controller;
        private List<String> vestigingen;
        private Vestiging vestiging1;
        private Vestiging vestiging2;
        private Vestiging vestiging3;
        private int totaalAantalKlanten;
        private String groningen;
        private String zuidhorn;
        private int klantengroningen;
        private int klantenzuidhorn;
        
        @BeforeEach
        public void setUp() throws ApplicatieException {
                vestigingen = controller.getVestigingLocaties();
                vestiging1 = new Vestiging("Amsterdam");
                vestiging2 = new Vestiging("Rotterdam");
                vestiging3 = new Vestiging("Amsterdam");
                totaalAantalKlanten = 0;
        }
        
        @AfterEach
        public void tearDown() {
                vestigingen = null;
                vestiging1 = null;
                vestiging2 = null;
        }
        
        private void setUpOpenClose() throws ApplicatieException {
                for (String vest : vestigingen) {
                        totaalAantalKlanten += controller.getKlantenDTO(vest).getAantalKlanten();
                        }
                groningen = "Groningen";
                zuidhorn = "Zuidhorn";
                klantengroningen = controller.getKlantenDTO(groningen).getAantalKlanten();
                klantenzuidhorn = controller.getKlantenDTO(zuidhorn).getAantalKlanten();
        }
         

        /**
         * Test om te verifieren dat een vestiging met een lege lijst klanten wel een DTO
         * aanmaakt waarbij de informatie aangeeft dat er geen klanten zijn.
         * @throws ApplicatieException
         */
//        @Test
//        public void geenKlantenTest() throws ApplicatieException {
//                Vestiging v = new Vestiging(groningen);
//                v.setKlanten(new ArrayList<Klant>());
//                KlantenDTO dto = v.getKlantenDTO();
//                assertNotNull("Er wordt wel een DTO aangemaakt", dto);
//                assertNotNull("Er wordt een klantnummerlijst gemaakt", dto.getKlantNummers());
//                assertEquals("Aantal klanten is nul", 0, dto.getAantalKlanten());
//        }
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
//        @Test
//        public void compareToTest() {
//                assertTrue("Vestiging 1 komt voor klant 2",  vestiging1.compareTo(vestiging2) <= 1);
//                assertTrue("Vestiging 2 komt voor 1", vestiging2.compareTo(vestiging1) >= 1);
//                assertTrue("Vestiging 1 en 2 zijn gelijk aan zichzelf", vestiging1.compareTo(vestiging1)
//                                == 0 && vestiging2.compareTo(vestiging2) == 0);
//
//                //Arrange
//                List<Klant> klanten1 = new ArrayList<Klant>();
//                for (int i = 0; i<10; i++) {
//                        klanten1.add(new Klant(i));
//                }
//                vestiging1.setKlanten(klanten1);
//                vestiging3.setKlanten(new ArrayList<Klant>());
//
//                assertTrue("Vestiging 1 komt voor vestiging 3", vestiging1.compareTo(vestiging3) <= 1);
//        }

}
