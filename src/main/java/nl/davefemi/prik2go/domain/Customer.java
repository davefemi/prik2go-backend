package nl.davefemi.prik2go.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Customer implements Comparable<Customer>{
    private long number;
    private List<String> closestLocations = new ArrayList<>();

    /**
     * Comparator methode voor deze klasse op basis van Klantnummer
     */
    @Override
    public int compareTo(Customer c) {
        return Integer.compare((int) this.number, (int) c.number);
    }
}
