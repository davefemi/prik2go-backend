package nl.davefemi.prik2go.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse die een klant vertegenwoordigt. Een klant heeft een
 * nummer en een vestiging.
 */
public class Klant implements Comparable<Klant> {
        private final int nummer;
        private List<String> dichtstbijzijndeLocaties;
        
        /**
         * Constructor voor Klant-object waarbij klantnummer en oorspronkelijke vestiging
         * locatie wordt geinitialiseerd
         * @param nummer
         */
        public Klant(int nummer) {
                this.nummer = nummer;
        }
        
        /**
         * Get-methode voor klantnummer
         * @return nummer
         */
        public int getNummer() {
                return nummer;
        }
                        
        /**
         * Toewijzing van de lijst van dichtstbijzijnde locaties gerangschikt naar
         * relatieve afstand tot deze klant
         * @param vestigingen
         */
        public void setDichtstbijzijndeLocaties(List<String> locaties) {
                dichtstbijzijndeLocaties = locaties;
        }
        
        /**
         * Keert lijst van dichtstbijzijnde locaties terug
         * @return dichtstbijzijnde vestiginglocaties
         */
        public List<String> getDichtbijzijndeLocaties(){
                List<String> locaties = new ArrayList<String>();
                dichtstbijzijndeLocaties.forEach(locaties::add);
                return locaties;
        }
                       
        /**
         * Comparator methode voor deze klasse op basis van Klantnummer
         */
        @Override
        public int compareTo(Klant k) {
                return Integer.compare(this.nummer, k.nummer);
        }
}
