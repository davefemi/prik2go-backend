package nl.davefemi.prik2go.data.mapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.repository.DBConnection;
import nl.davefemi.prik2go.domain.Klant;
import nl.davefemi.prik2go.domain.Vestiging;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;


/**
 * Deze klasse is verantwoordelijk voor de communicatie met de Prik2Go-database
 */
@Component
@RequiredArgsConstructor
public class DatabaseMapper {
        private final DBConnection dbConnection;
        private static final Logger logger = Logger.getLogger(DatabaseMapper.class.getName());
        private static final String SQL_VESTIGING = 
                        "SELECT plaats FROM Vestiging";
        private static final String SQL_KLANTEN =
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
        private static final String SQL_DICHTSTBIJZIJNDEVESTIGINGEN =
                        "SELECT          V.plaats " +
                        "FROM            Klant K " +
                                                "JOIN Postcodeinfo KP ON K.postcode = KP.postcode, " +
                                                "Vestiging V " +
                                                "JOIN Postcodeinfo VP ON V.postcode = VP.postcode " +
                        "WHERE           K.nr = ? " +
                        "ORDER BY        SQRT(POWER(KP.lat-VP.lat,2) + POWER(KP.lng-VP.lng,2)) ASC";
        
        private Connection connection;
        private PreparedStatement psSQLvestiging;
        private PreparedStatement psSQLklanten;
        private PreparedStatement psSQLdichtstVestigingen;

        /**
         * Verbinding met database wordt gezocht en PreparedStatement-objecten
         * worden geinitialiseerd.
         * @throws ApplicatieException wordt opgegegooid door klasse DBConnection als er een
         * fout optreedt met de driver of verbinding.
         */
        @PostConstruct
        private void initVerbinding() throws ApplicatieException {
                connection = dbConnection.maakVerbinding();
                initPreparedStatements();
                logger.info("Verbinden met database succesvol");
        }
        
        /**
         * Verbinding met de database wordt verbroken.
         * @throws ApplicatieException
         */
        private void sluitVerbinding() throws ApplicatieException {
                if (connection != null) {
                        try {
                                connection.close();
                                logger.info("Sluiten verbinding met database succesvol");
                                }
                        catch (SQLException e){
                                logger.info(e.getMessage());
                                throw new ApplicatieException("Fout opgetreden bij het verbreken van de verbinding met de database.");
                        }
                }
        }
        
        /**
         * PreparedStatement-objecten worden geinitialiseerd met behulp van het verkregen connection-
         * attribuut.
         * @throws ApplicatieException als initialisering mislukt.
         */
        private void initPreparedStatements() throws ApplicatieException {
                try {
                        psSQLvestiging = connection.prepareStatement(SQL_VESTIGING);
                        psSQLklanten = connection.prepareStatement(SQL_KLANTEN);
                        psSQLdichtstVestigingen = connection.prepareStatement(SQL_DICHTSTBIJZIJNDEVESTIGINGEN);
                }
                catch (SQLException e){
                        sluitVerbinding();
                        throw new ApplicatieException("Fout bij initialisering prepared statements.");      
                }
        }
        
        /**
         * Database wordt gelezen op alle beschikbare vestigingen.
         * @return dataset in de vorm van een lijst met alle vestigingslocaties.
         * @throws ApplicatieException als het uitvoeren van de query mislukt.
         */
        public Map<String, Vestiging> leesAlleVestigingen() throws ApplicatieException {
                Map<String, Vestiging> vestigingen = new TreeMap<String, Vestiging>();
                try {
                        if (connection.isClosed()) {
                                initVerbinding();
                        }
                        ResultSet res = psSQLvestiging.executeQuery();
                        while (res.next()) {      
                                String plaats = res.getString("plaats");
                                vestigingen.put(plaats, new Vestiging(plaats));
                        }
                        logger.info("Lezen vestigingen succesvol");
                }
                catch (SQLException e) {
                        logger.warning(e.getMessage());
                        throw new ApplicatieException("Fout bij ophalen vestigingen");
                }
                finally {
                        sluitVerbinding();
                }
                return vestigingen;
        }
        
        /**
         * Aan de hand van een gegeven Vestiging-object wordt de database bevraagd
         * voor de unieke klanten die daarbij horen. 
         * @param vestiging waarvan de klanten ingelezen moeten worden.
         * @return alle unieke klanten van deze vestiging die opgeslagen zijn in de database.
         * @throws ApplicatieException
         */
        public List<Klant> leesKlanten(Vestiging vestiging) throws ApplicatieException {
                ArrayList<Klant> klanten = new ArrayList<Klant>();
                try {
                        if (connection.isClosed()) {
                                initVerbinding();  
                        }
                        psSQLklanten.setString(1, vestiging.getLocatie());
                        ResultSet res = psSQLklanten.executeQuery();
                        
                        while (res.next()) {
                                klanten.add(new Klant(res.getInt("klant")));
                        }
                        
                        for (Klant k : klanten) {
                                k.setDichtstbijzijndeLocaties(leesDichtstbijzijndeVestigingen(k));
                                }
                        logger.info("Lezen klanten voor [" +vestiging.getLocatie() + "] succesvol");
                        }
                catch (SQLException e) {
                        logger.warning("Fout bij lezen klanten voor [" + vestiging.getLocatie() + "]: " + e.getMessage());
                        throw new ApplicatieException("Fout bij inlezen klanten");
                }
                catch (NullPointerException e) {
                        throw new ApplicatieException("Geen vestiginginformatie meegegeven");
                }
                finally {
                        sluitVerbinding();
                }
                vestiging.setKlanten(klanten);
                return klanten;
        }
        
        
        /**
         * Deze hulpmethode voegt per een lijst met een rangschikking naar relatieve afstand van alle vestigingen
         * @param klant
         * @throws ApplicatieException
         */
        private List<String> leesDichtstbijzijndeVestigingen(Klant klant) throws ApplicatieException {
                ArrayList<String> vestigingen = new ArrayList<String>();
                try {
                        psSQLdichtstVestigingen.setInt(1, klant.getNummer());
                        ResultSet res = psSQLdichtstVestigingen.executeQuery();
                        while (res.next()) {
                                vestigingen.add(res.getString("plaats"));
                                }
                        }
                catch (SQLException e)
                {
                        throw new ApplicatieException("Fout bij inlezen dichtstbijzijnde vestigingen");
                }
                return vestigingen;
        }
}
