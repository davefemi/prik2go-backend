package nl.davefemi.prik2go;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.junit.After;
import org.junit.Before;
import nl.davefemi.prik2go.data.repository.DBConnection;
import nl.davefemi.prik2go.data.mapper.domain.DatabaseMapper;
import nl.davefemi.prik2go.domain.Klant;
import nl.davefemi.prik2go.domain.Vestiging;
import nl.davefemi.prik2go.exceptions.ApplicatieException;

/**
 * Testklasse voor klasse VestigingMapper
 */
@SpringBootTest
@RequiredArgsConstructor
public class VestigingMapperTest  {
        @Autowired
        private DBConnection dbConnection;
        @Autowired
        private DatabaseMapper mapper;
        private Map<String, Vestiging> vestigingen;
        private Connection connection;
        private List<Klant> klanten;
        private static final String SQLVESTIGING =
                        "SELECT plaats FROM Vestiging";
        private static final String SQLKLANTEN =
                        "SELECT          DISTINCT klant " +
                        "FROM            Bezoek B " +
                        "WHERE           ? IN  " +
                                               "(SELECT        BZ.vestiging " +
                                               "FROM           Bezoek BZ " +
                                                              "JOIN Klant K ON BZ.klant = K.NR " +
                                                              "JOIN Postcodeinfo KP ON K.postcode = KP.postcode " +
                                                              "JOIN Vestiging V ON BZ.vestiging = V.plaats " +
                                                              "JOIN Postcodeinfo VP ON V.postcode = VP.postcode " +
                                               "WHERE           BZ.klant = B.klant " +
                                               "ORDER BY        SQRT(POWER(KP.lat-VP.lat,2) + POWER(KP.lng-VP.lng,2)) ASC " +
                                               "ROWS            1)"; 
        private static final String SQLDICHTSTBIJZIJNDEVESTIGINGEN =
                        "SELECT          V.plaats " +
                        "FROM            Klant K " +
                                        "JOIN Postcodeinfo KP ON K.postcode = KP.postcode, " +
                                        "Vestiging V " +
                                        "JOIN Postcodeinfo VP ON V.postcode = VP.postcode " +
                        "WHERE           K.nr = ? " +
                        "ORDER BY        SQRT(POWER(KP.lat-VP.lat,2) + POWER(KP.lng-VP.lng,2)) ASC";
        private PreparedStatement psVestiging;
        private PreparedStatement psKlanten;
        private PreparedStatement psDichtstbijzijndeVestigingen;
        
        @Before
        public void setUp() throws ApplicatieException, SQLException, NoSuchFieldException, SecurityException {
                init();
                buildVestigingMap();
        }
        
        private void init() throws ApplicatieException, SQLException, NoSuchFieldException, SecurityException {
                vestigingen = mapper.leesAlleVestigingen();
                connection = dbConnection.maakVerbinding();
                klanten = new ArrayList<Klant>();
                psVestiging = connection.prepareStatement(SQLVESTIGING);
                psKlanten = connection.prepareStatement(SQLKLANTEN);
                psDichtstbijzijndeVestigingen = connection.prepareStatement(SQLDICHTSTBIJZIJNDEVESTIGINGEN);
        }
        
        private void buildVestigingMap() throws ApplicatieException {
                for (Map.Entry<String, Vestiging> v: vestigingen.entrySet()) {
                        klanten.addAll(mapper.leesKlanten(v.getValue()));
                }
        }
                
        @After
        public void TearDown() throws SQLException {
                connection.close();
        }
        
        /**
         * Eenvoudige test die verifieert dat er daadwerkelijk vestigingen zijn opgehaald
         * @throws ApplicatieException
         */
        @Test
        public void leesVestigingenTest() throws ApplicatieException {
                assertNotNull("Er zijn vestigingen opgehaald", vestigingen);
        }
        
        /**
         * Test om de waarden uit de tabel voor vestigingen te vergelijken met de waarden die uiteindelijk
         * in de applicatie worden geladen. Er wordt gekeken of alle vestigingen uit de database voorkomen in
         * de map die is ingelezen en andersom of alle waarden in die map terug zijn te vinden in de database
         * @throws SQLException
         */
        @Test
        public void vergelijkVestigingenTest() throws SQLException {
                List<String> vestigingenDatabase = new ArrayList<String>();
                ResultSet res = psVestiging.executeQuery();
                while (res.next()) {
                        String plaats = res.getString("plaats");
                        vestigingenDatabase.add(plaats);
                        //Assert
                        assertTrue("Vestigingen is ingelezen", vestigingen.containsKey(plaats));
                }
                for (String s : vestigingen.keySet()) {
                        //Assert
                        assertTrue("Vestigingen zijn in database te vinden", vestigingenDatabase.contains(s));
                }
        }
        
        /**
         * Test om de verkregen klanten per vestigingobject te controleren op juistheid volgens de informatie in
         * de database. Daarvoor wordt een query uitgevoerd en de map met waarden per vestiging vergeleken met de 
         * waarden die in de KlantenDTO zijn te lezen.
         * @throws SQLException
         */
        @Test
        public void vergelijkKlantenTest() throws SQLException {
                Map<String, List<Integer>> vestigingenDatabase = new TreeMap<String, List<Integer>>();
                for (String locatie : vestigingen.keySet()) {
                        psKlanten.setString(1, locatie);
                        ResultSet res = psKlanten.executeQuery();
                        List<Integer> klanten = new ArrayList<Integer>();
                        while (res.next()) {
                                int klant = res.getInt("klant");
                                klanten.add(klant);
                        }
                        vestigingenDatabase.put(locatie, klanten);
                        //Assert
                        assertEquals("Klantnummers komen overeen", vestigingenDatabase.get(locatie),
                                        vestigingen.get(locatie).getKlantenDTO().getKlantNummers());
                }
        }
        
        /**
         * Eenvoudige test om te verifieren of er daadwerkelijk klanten worden opgehaald bij een vestiging.
         * @throws ApplicatieException
         */
        @Test
        public void leesKlantenTest() throws ApplicatieException{
                for (Map.Entry<String, Vestiging> v : vestigingen.entrySet()) {
                        //Assert
                        assertFalse("Er zijn klanten opgehaald bij deze vestiging", mapper.leesKlanten(v.getValue()) == null);
                }
        }
        
        /**
         * Test om te verifieren of de toegewezen dichtstbijzijnde vestigingen per klant overeenkomen met
         * de informatie die daarover in de database staat.
         * @throws SQLException
         * @throws IllegalAccessException 
         * @throws IllegalArgumentException 
         */
        @Test
        public void dichtstbijzijndeVestigingenTest() throws SQLException, IllegalArgumentException, IllegalAccessException {
                for (Klant k:klanten) {
                        List<String> dichtstbijzijndeVestigingenDatabase = new ArrayList<String>();
                        psDichtstbijzijndeVestigingen.setInt(1, k.getNummer());
                        ResultSet res = psDichtstbijzijndeVestigingen.executeQuery();
                        while (res.next()) {
                                dichtstbijzijndeVestigingenDatabase.add(res.getString("plaats"));
                        }
                        for (int i = 0; i<dichtstbijzijndeVestigingenDatabase.size(); i++) {              
                                List<String> dichtbijVest = k.getDichtbijzijndeLocaties();
                                //Assert
                                assertEquals("Dichtstbijzijnde vestigingen komen overeen", dichtstbijzijndeVestigingenDatabase.get(i), 
                                                dichtbijVest.get(i));
                        }
                }
                
        }
        
        /**
         * Deze test controleert of de correct exceptie wordt opgegooid bij het meegeven van nullwaarden
         * als actuele parameters bij de methode leesKlanten().
         */
        @Test
        public void exceptionsTest() {
                ApplicatieException e = assertThrows(ApplicatieException.class, ()-> {
                        mapper.leesKlanten(null);
                }) ;
                assertEquals("Geen vestiginginformatie meegegeven", e.getMessage());
        }
}
