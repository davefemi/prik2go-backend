package nl.davefemi.prik2go.domain;

import nl.davefemi.prik2go.data.dto.KlantenDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Klasse die een vestiging vertegenwoordigt. Dit object is ook
 * verantwoordelijk voor het beheren van de bijbehorende klanten
 * en desgevraagd zal een KlantenDTO kunnen geven waar de gegevens 
 * en het totaalaantal uit te lezen valt. Een Vestiging kan oorspronkelijke 
 * klanten hebben en huidige klanten. In eerste instantie zijn beide 
 * attributen gelijk.Er kunnen aan de huidige klanten nieuwe objecten 
 * worden toegevoegd en ook objecten worden verwijderd.
 */
public class Vestiging implements Comparable<Vestiging> {
        private static final Logger logger = Logger.getLogger(Vestiging.class.getName());
        private final String locatie;
        private boolean open = true;
        private List<Klant> oorspronkelijkeKlanten = null;
        private List<Klant> huidigeKlanten = null;
        
        /**
         * Constructor voor deze klasse
         * @param locatie van deze Vestiging.
         */
        public Vestiging (String locatie) {
                this.locatie = locatie;
        }
        
        /**
         * Keert de locatie van deze Vestiging terug
         * @return locatie
         */
        public String getLocatie() {
                return locatie;
        }
        
        /**
         * Set methode voor het ingelezen List<Klant> object met klanten voor dit 
         * Vestiging-object
         * @param klanten
         */
        public synchronized void setKlanten(List<Klant> klanten) {
                oorspronkelijkeKlanten = klanten;
                huidigeKlanten = new ArrayList<Klant>();
                oorspronkelijkeKlanten.forEach(huidigeKlanten::add);              
        }
             
        /**
         * Keert de List<Klant> met huidige Klant objecten terug 
         * @return List<Klant> huidige klanten
         */
        public synchronized List<Klant> getHuidigeKlanten(){
                List<Klant> klanten = new ArrayList<Klant>();
                huidigeKlanten.forEach(klanten::add);
                return klanten;
        }
        
        public synchronized boolean heeftKlant(Klant k) {
                return huidigeKlanten.contains(k);
        }
        
        /**
         * Keert een lijst met oorspronkelijke Klant objecten terug 
         * @return List<Klant> oorspronkelijke klanten
         */
        public synchronized List<Klant> getOorspronkelijkeKlanten(){
                List<Klant> klanten = new ArrayList<Klant>();
                oorspronkelijkeKlanten.forEach(klanten::add);
                return klanten;
        }
                
        /**
         * Creatie van een nieuw KlantenDTO-object
         * @return KlantenDTO
         */
        public KlantenDTO getKlantenDTO(){
                List<Integer> klantNummers = new ArrayList<Integer>();
                if (huidigeKlanten != null) {
                        synchronized (this) {
                                for (Klant k : huidigeKlanten) {
                                        klantNummers.add(k.getNummer());
                                }
                                Collections.sort(klantNummers);
                        }
                }
                return new KlantenDTO(klantNummers); 
        }
        
        /**
         * Status van deze Vestiging wordt 'open' en oorspronkelijke klanten
         * worden toegevoegd aan aan de lijst voor huidige klanten.
         */
        public synchronized void setOpen() {
                if (!open) {
                        open = true;
                        if (oorspronkelijkeKlanten != null) {
                                oorspronkelijkeKlanten.forEach(this::voegKlantToe);
                                logger.info("Vestiging [" + locatie + "] is heropend");
                                }
                        } 
        }
        
        /**
         * Status van deze Vestiging wordt 'gesloten' en lijst van huidige klanten wordt
         * geleegd.
         */
        public synchronized void setClosed() {
                if (open) {
                        open = false;
                        logger.info("Vestiging [" + locatie + "] is gesloten");
                        if (huidigeKlanten!= null) {
                                huidigeKlanten.clear();
                        }
                }
        }
        
        /**
         * Keert de status van de Vestiging terug als boolean. De waarde true betekent dat
         * de vestiging open is. De waarde false betekent dat de vestiging gesloten is.
         * @return true of false
         */
        public synchronized boolean isOpen() {
                return open;
        }
        
        /**
         * Methode waarmee een Klant-object kan worden toegevoegd als klant. Een nieuwe klant
         * wordt alleen toegevoegd aan de huidige klanten. De oorspronkelijke klanten blijven
         * daarmee ongewijzigd.
         * @param klant
         */
        public synchronized void voegKlantToe(Klant klant) {
                if (huidigeKlanten != null ) {
                        huidigeKlanten.add(klant);
                }
        }
        
        /**
         * Methode waarmee een Klant-object wordt verwijderd als huidige klant. De oorspronkelijke
         * klanten blijven ongewijzigd.
         * @param klant
         */
        public synchronized void verwijderKlant(Klant klant) {
                if (huidigeKlanten != null) {
                        huidigeKlanten.remove(klant);
                }
        }

        /**
         * Levert de locatie en aantal klanten van deze Vestiging als String waarde.
         */
        @Override 
        public synchronized String toString(){
                return locatie + huidigeKlanten.size();
        }

        /**
         * Comparator methode primair op basis van Vestigingslocatie en secundair op
         * basis van aantal klanten.
         */
        @Override
        public int compareTo(Vestiging v) {
                synchronized (this) {
                        synchronized (v) {
                                if (this.getLocatie().equals(v.getLocatie()) && this.huidigeKlanten != null
                                        && v.huidigeKlanten != null) {
                                        return ((Integer) this.huidigeKlanten.size()).compareTo((Integer) v.huidigeKlanten.size());
                                }
                        }
                }
                return this.locatie.compareTo(v.locatie);
        }
}
