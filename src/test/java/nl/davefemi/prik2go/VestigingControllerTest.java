package nl.davefemi.prik2go;

import org.junit.jupiter.api.Assertions;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import nl.davefemi.prik2go.exceptions.VestigingException;
import nl.davefemi.prik2go.service.domain.DomainService;
import nl.davefemi.prik2go.data.repository.DBConnection;
import nl.davefemi.prik2go.data.mapper.domain.DatabaseMapper;
import nl.davefemi.prik2go.domain.Klant;
import nl.davefemi.prik2go.domain.Vestiging;
import nl.davefemi.prik2go.exceptions.ApplicatieException;

/**
 * Testklasse voor klasse VestigingController
 */
@SpringBootTest
public class VestigingControllerTest {
        @Autowired
        private DBConnection dbConnection;
        @Autowired
        private DomainService controller;
        @Autowired
        private DatabaseMapper mapper;
        private Connection connection;
        private List<String> vestigingen;
        private Map<String, Vestiging> vestigingObjecten;
        private List<Klant> klantObjecten;
        private Map<Integer, List<String>> dbvPerKlant;
        private Map<String, List<Integer>> oorspronkelijkeKlanten;
        
        /**
         * Bouwt de attributen voor elk uit te voeren test op
         * @throws ApplicatieException
         * @throws SQLException
         */
        @BeforeEach
        public void buildUp() throws ApplicatieException, SQLException {
                init();
                buildVestigingObjecten();
                buildKlantObjecten();
        }
        
        /**
         * Initialisatie van de verschillende objecten
         * @throws ApplicatieException
         * @throws SQLException
         */
        private void init() throws ApplicatieException, SQLException {
                connection = dbConnection.maakVerbinding();
                vestigingObjecten = mapper.leesAlleVestigingen();
                vestigingen = controller.getVestigingLocaties();
                klantObjecten = new ArrayList<Klant>();
                dbvPerKlant = new HashMap<Integer, List<String>>();
                oorspronkelijkeKlanten = new HashMap<String, List<Integer>>();
        }
        
        /**
         * Vestigingobjecten worden aangemaakt
         * @throws ApplicatieException
         */
        private void buildVestigingObjecten() throws ApplicatieException {
                for (Map.Entry<String, Vestiging> v : vestigingObjecten.entrySet()) {
                        klantObjecten.addAll(mapper.leesKlanten(v.getValue()));
                        oorspronkelijkeKlanten.put(v.getKey(), controller.getKlantenDTO(v.getKey()).getKlantNummers());
                }
        }
        
        /**
         * Klantobjecten worden aangemaakt.
         * @throws SQLException
         */
        private void buildKlantObjecten() throws SQLException {
                for (Klant k: klantObjecten) {
                        List<String> dbv = new ArrayList<String>();
                        k.getDichtbijzijndeLocaties().forEach(dbv::add);
                        dbvPerKlant.put(k.getNummer(), dbv);
                }  
        }
        
        /**
         * Sluit verbinding na elke test.
         * @throws SQLException
         */
        @AfterEach
        public void tearDown() throws SQLException {
                connection.close();
        }
        
        /**
         * Verifieert dat er vestigingen zijn geinitialiseerd.
         */
        @Test
        public void vestigingenIngelezenTest() {
                Assertions.assertNotNull(controller.getVestigingLocaties(), "Vestigingen zijn ingelezen");
        }
        
        
        /**
         * Test om te controleren dat er een KlantenDTO wordt aangemaakt bij aanroepen van de methode.
         * @throws ApplicatieException
         */
        @Test
        public void getKlantenDTO() throws ApplicatieException {
                for (String v : controller.getVestigingLocaties()) {
                        Assertions.assertNotNull( controller.getKlantenDTO(v), "KlantenDTO wordt aangemaakt");
                }
        }
        
        /**
         * Deze test controleert of vestigingen daadwerkelijk geen klanten meer hebben als de vestiging wordt gesloten. 
         * Per vestiging wordt de status veranderd naar gesloten en wordt de KlantenDTO gelezen voor het aantal klanten.
         * @throws ApplicatieException
         */
//        @Test
//        public void sluitAlleVestigingenTest() throws ApplicatieException, VestigingException {
//                for (String vestiging : vestigingen) {
//                        controller.veranderVestigingStatus(vestiging);
//
//                        Assertions.assertTrue(controller.getKlantenDTO(vestiging).getAantalKlanten() == 0, "Vestiging heeft geen klanten meer");
//                }
//        }
        
        /**
         * Deze test controleert of klanten worden teruggeplaatst bij hun oorspronkelijke vestiging bij het heropenen van
         * hun vestiging. Daarvoor worden alle vestigingen gesloten en daarna weer direct geopend en wordt eerst per vestigingen
         * gezelen of het aantal klanten overeenkomt het het oorspronkelijke aantal. Voorts wordt er per oorspronkelijke
         * klant van de vestiging nagegaan of die terug te vinden in is de KlantenDTO van de heropende vestiging.
         * @throws SQLException
         * @throws ApplicatieException
         */
//        @Test
//        public void heropenOorspronkelijkeKlantenTest() throws SQLException, ApplicatieException, VestigingException {
//                vestigingen.forEach(t -> {
//                    try {
//                        controller.veranderVestigingStatus(t);
//                    } catch (VestigingException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//                for (String vestiging : vestigingen) {
//                        controller.veranderVestigingStatus(vestiging);
//                        //Assert
//                        Assertions.assertEquals(oorspronkelijkeKlanten.get(vestiging).size(),
//                                controller.getKlantenDTO(vestiging).getAantalKlanten(),
//                                "Aantal klanten is gelijk aan het aantal oorspronkelijke klanten");
//                        List<Integer> gewijzigdeKlantenLijst;
//                        for (String updatedVestiging: vestigingen) {
//                                gewijzigdeKlantenLijst = controller.getKlantenDTO(updatedVestiging).getKlantNummers();
//                                if (controller.getVestigingStatus(updatedVestiging)) {
//                                        for (Integer klant : oorspronkelijkeKlanten.get(updatedVestiging)) {
//                                                //Assert
//                                                Assertions.assertTrue(gewijzigdeKlantenLijst.contains(klant),
//                                                        "Klant is bij oorspronkelijke Vestiging");
//                                        }
//                                }
//                        }
//                }
//        }
        
        /**
         * Deze test controleert of klanten bij een sluiting van een vestiging naar hun dichtstbijzijnde vestiging
         * gaan. Deze lijst is per klant opgevraagd. De vestigingen worden 1 voor 1 gesloten en per sluiting wordt per klant 
         * gekeken wat de eerste open vestiging in hun lijst van dichtstbijzijnde vestigingen is. Daarbij wordt niet gekeken 
         * naar klanten waarvan hun oorspronkelijke vestiging open is. Ook worden gevallen niet meegenomen waarbij alle vestigingen 
         * gesloten zijn en er bij klanten dus geen waarde kan worden toegewezen aan de eerste open vestiging in hun lijst van 
         * dichtstbijzijnde vestigingen.
         * @throws ApplicatieException
         */
//        @Test
//        public void vestigingWisselTest() throws VestigingException {
//                for (String vestiging: vestigingen) {
//                        controller.veranderVestigingStatus(vestiging);
//                        List<Integer> gewijzigdeKlantenLijst = new ArrayList<Integer>();
//                        for (String updatedVestiging: vestigingen) {
//                                try {
//                                        gewijzigdeKlantenLijst = controller.getKlantenDTO(updatedVestiging).getKlantNummers();
//                                }
//                                catch (ApplicatieException e) {
//
//                                }
//                                for (Integer klant: gewijzigdeKlantenLijst) {
//                                        List<String> dbvDatabase = dbvPerKlant.get(klant);
//                                        for (String vest: dbvDatabase) {
//
//                                                String eerstOpenVestiging = "";
//                                                if (controller.getVestigingStatus(vest) &&
//                                                                !oorspronkelijkeKlanten.get(vest).contains(klant)) {
//                                                        eerstOpenVestiging = vest;
//                                                        break;
//                                                }
//                                                if (!eerstOpenVestiging.equals("")) {
//                                                        //Assert
//                                                        assertEquals ("Klant " + klant + " is bij dichtstbijzijnde vestiging en ",
//                                                                eerstOpenVestiging, updatedVestiging);
//                                                }
//                                        }
//                                }
//                        }
//                }
//        }
        
        /**
         * Deze test controleert op duplicate klanten bij openen/sluiten van vestigingen. Allereerst worden alle vestigingen een
         * voor een gesloten, waarbij er naar elke sluiting wordt gecontroleerd of er bij de opengebleven vestigingen geen duplicate 
         * klanten zijn. In tweede instantie worden alle gesloten vestigingen weer na elkaar geopend, waarbij dezelfde check plaatstvindt.
         * @throws ApplicatieException
         */
//        @Test
//        public void duplicateKlantenTest() throws ApplicatieException, VestigingException {
//                for (String vestiging : vestigingen) {
//                        controller.veranderVestigingStatus(vestiging);
//                        for (String vest : vestigingen) {
//                                if (controller.getVestigingStatus(vest)) {
//                                        List<Integer> klantnummers = controller.getKlantenDTO(vest).getKlantNummers();
//                                        for (int klant : klantnummers) {
//                                                for (String andereVestiging : vestigingen) {
//                                                        if (!andereVestiging.equals(vest)) {
//                                                                //Assert
//                                                                assertTrue("Klant bevindt zich niet in een andere locatie",
//                                                                                !controller.getKlantenDTO(andereVestiging).
//                                                                                getKlantNummers().contains(klant));
//                                                                }
//                                                        }
//                                                }
//                                        }
//                                }
//                }
//                for (String vestiging : vestigingen) {
//                        controller.veranderVestigingStatus(vestiging);
//                                        List<Integer> klantnummers = controller.getKlantenDTO(vestiging).getKlantNummers();
//                                        for (int klant : klantnummers) {
//                                                for (String andereVestiging : vestigingen) {
//                                                        if (controller.getVestigingStatus(andereVestiging) && !andereVestiging.equals(vestiging)) {
//                                                                //Assert
//                                                                assertTrue("Klant bevindt zich niet in een andere locatie",
//                                                                                !controller.getKlantenDTO(andereVestiging).
//                                                                                getKlantNummers().contains(klant));
//                                                                }
//                                                        }
//                                                }
//                                        }
//                }
        
        /**
         * Deze test is verwant aan de bovenstaande. Hier wordt tijdens 1500 iteraties telkens een willekeurige vestiging gesloten
         * of heropend. Hierbij wordt gecheckt op duplicate waarden. Ook wordt gecontroleerd of de gesloten vestigingen geen klanten
         * meer bevatten.
         * @throws ApplicatieException
         */
//        @Test
//        public void randomizerTest() throws ApplicatieException, VestigingException {
//                Random randomizer = new Random();
//                for (int i = 0; i<1500; i++) {
//                        controller.veranderVestigingStatus(vestigingen.get(randomizer.nextInt(12)));
//                        //Voor elke vestiging wordt gecontroleerd
//                        for (String vest : vestigingen) {
//                                if (controller.getVestigingStatus(vest)) {
//                                        List<Integer> klantnummers = controller.getKlantenDTO(vest).getKlantNummers();
//                                        //Klantnummers van de vestiging
//                                        for (int klant : klantnummers) {
//                                                //Vergelijking klantnummers met andere vestigingen
//                                                for (String andereVestiging : vestigingen) {
//                                                        if (!andereVestiging.equals(vest)) {
//                                                                //Assert
//                                                                Assertions.assertTrue(!controller.getKlantenDTO(andereVestiging).
//                                                                                getKlantNummers().contains(klant), "Klant bevindt zich niet in een andere locatie");
//                                                                }
//                                                        }
//                                                }
//                                        }
//                                if(!controller.getVestigingStatus(vest)){
//                                        //Assert
//                                        Assertions.assertTrue(controller.getKlantenDTO(vest).getKlantNummers().isEmpty(),
//                                                "Vestiging heeft geen klanten");
//                                        }
//                                }
//                        }
//        }
        

        /**
         * Deze test controleert of de correcte exceptie wordt opgegooid bij een nullwaarde als vestiginglocatie.
         */
//        @Test
//        public void exceptionsTest() {
//                ApplicatieException e = assertThrows(ApplicatieException.class, () -> {
//                        controller.getKlantenDTO(null);
//                });
//                assertEquals("Geen vestiging gekozen", e.getMessage());
//        }
}
