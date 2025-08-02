package nl.davefemi.prik2go.data.dto;

import java.util.List;

/**
 * Data Transfer Object voor klantgegevens van een vestiging
 * Alle klantnummers worden in een lijst opgenomen en het aantal 
 * klanten opgeslagen.
 */
public class KlantenDTO {
        private List<Integer> klantNummers;
        private int aantalKlanten;
       
        /**
         * Constructor voor KlantenDTO
         * @param klantNummers
         */
        public KlantenDTO(List<Integer> klantNummers) {
                this.klantNummers = klantNummers;
                aantalKlanten = klantNummers.size();
        }
          
        /**
         * Keert een lijst met klantnummers terug
         * @return klantnummers
         */
        public List<Integer> getKlantNummers(){
                return klantNummers;
        }
        
        /**
         * Geeft het aantal klanten bij deze vestiging
         * @return aantal klanten
         */
        public int getAantalKlanten() {
                return aantalKlanten;
        }
}
