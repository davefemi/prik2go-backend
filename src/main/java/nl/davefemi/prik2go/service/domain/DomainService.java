package nl.davefemi.prik2go.service.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.mapper.DatabaseMapper;
import nl.davefemi.prik2go.domain.Klant;
import nl.davefemi.prik2go.data.dto.KlantenDTO;
import nl.davefemi.prik2go.domain.Vestiging;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Deze klasse is verantwoordelijk voor de controllerfunctie.
 * Het object beheert de vestigingsobjecten en verzorgt de
 * creatie en communicatie met de mapperklasse.
 */
@Service
@RequiredArgsConstructor
public class DomainService {
        private static final Logger logger = Logger.getLogger(DomainService.class.getName());
        private static final Log log = LogFactory.getLog(DomainService.class);
        private final DatabaseMapper mapper;
        private Map<String, Vestiging> vestigingen = null;

        /**
         * Verzorgt de initialisatie van de Vestigings-arraylist.
         * @throws ApplicatieException
         */
        @PostConstruct
        private void initVestigingen() throws ApplicatieException {
                vestigingen = mapper.leesAlleVestigingen();
                Map<String, Vestiging> kopieLijst = new TreeMap<String, Vestiging>();
                vestigingen.forEach(kopieLijst::put);
                for (Map.Entry <String, Vestiging> v : vestigingen.entrySet()) {
                        mapper.leesKlanten(v.getValue()); 
                        logger.info(v.getValue().getKlantenDTO().getAantalKlanten() +
                                " klanten voor [" + v.getKey() + "] zijn ingeladen");
                        }
                ;
        }

        /**
         * Geeft een lijst met de vestigingslocaties terug.
         * @return List<String> plaatsnamen
         */
        public List<String> getVestigingLocaties() {
                List<String> locaties = new ArrayList<String>();
                vestigingen.keySet().forEach(locaties::add);
                return locaties;
        }
        
                
        /**
         * Geeft de KlantenDTO voor de gegeven locatie terug. Als er geen locatie gekozen is, of de 
         * terugkeerwaarde van de dto is null, wordt een exceptie opgegooid.
         * @param String locatie van de vestiging
         * @return KlantenDTO met klantgegevens voor de gegeven locatie
         * @throws ApplicatieException
         */
        public KlantenDTO getKlantenDTO(String locatie) throws ApplicatieException {
                Vestiging vestiging;
                if (locatie != null) {
                        vestiging = vestigingen.get(locatie);
                        if (vestiging != null){
                                return vestiging.getKlantenDTO();
                        }
                }
                throw new ApplicatieException ("Invalid branch");
        }
        
        /**
         * Geeft de status van de vestiging terug Als de vestiging open is, zal de status 'true'
         * zijn. Als een vestiging gesloten is, zal de status 'false' zijn.
         * @param String locatie
         * @return boolean
         */
        public boolean getVestigingStatus(String locatie) throws VestigingException {
                if (!vestigingen.containsKey(locatie)){
                        throw new VestigingException("Branch does not exist");
                }
                return vestigingen.get(locatie).isOpen();
        }
        
        /**
         * Methode zal aan de hand van de huidige vestigingsstatus kiezen tussen een sluiting
         * of heropening van de gegeven locatie.
         * @param String locatie
         * @throws ApplicatieException 
         * @throws VestigingException als alle vestigingen status 'gesloten' bereiken wordt deze 
         * exceptie opgegooid
         */
        public void veranderVestigingStatus(String locatie) throws VestigingException{
                if (!vestigingen.containsKey(locatie)){
                        throw new VestigingException("Branch does not exist");
                }
                if(vestigingen.get(locatie).isOpen()) {
                        AtomicInteger aantalVestigingenOpen = new AtomicInteger();
                        vestigingen.values().forEach(v -> {if(v.isOpen()){
                                aantalVestigingenOpen.getAndIncrement();
                        }});
                        if(aantalVestigingenOpen.get() > 1) {sluitVestiging(locatie);}
                        else {
                                logger.warning("Cannot close [" + locatie + "]. Closing all locations is not allowed");
                                throw new VestigingException("Er moet minstens 1 vestiging open blijven");
                        }
                        }
                else {
                        openVestiging(locatie);
                        }
                }
        
        /**
         * Methode om een Vestiging te sluiten en de bijbehorende klanten te plaatsen bij een andere
         * vestiging. Per huidige Klant van de gegeven vestiging wordt de interne lijst van dichtstbijzijnde
         * vestigingen opgevraagd. Voor deze vestigingen geldt dat de eerste die open is en niet gelijk is
         * aan de huidige vestiging, de nieuwe vestiging wordt. Als er geen vestigingen open zijn,
         * wordt er geen vestiging toegewezen. De vestiging wordt gesloten.
         * @param String locatie
         */
        private void sluitVestiging(String locatie) {
                Vestiging vestiging = vestigingen.get(locatie);
                for (Klant k : vestiging.getHuidigeKlanten()) {
                        for (String vest: k.getDichtbijzijndeLocaties()) {
                                if (vestigingen.get(vest).isOpen() && !vestigingen.get(vest).equals(vestiging)) {
                                        vestigingen.get(vest).voegKlantToe(k);
                                        break;
                                }
                        }
                }
                vestiging.setClosed();
        }
        
        /**
         * Methode om een Vestiging te heropenen en de bijbehorende oorspronkelijke klanten te verwijderen bij
         * de overige vestigingen, voorzover die daar te vinden zijn. De Vestiging zal zijn eigen oorspronkelijke 
         * klanten weer instellen als huidige klanten.
         * @param String locatie
         */
        private void openVestiging(String locatie) {
                Vestiging vestiging = vestigingen.get(locatie);
                for (Klant k : vestiging.getOorspronkelijkeKlanten()) {
                        for (Vestiging vest : vestigingen.values()) {
                                if (vest.heeftKlant(k) && !vest.equals(vestiging)) {
                                        vest.verwijderKlant(k);
                                }
                        }
                }
                vestiging.setOpen();
        }
}
