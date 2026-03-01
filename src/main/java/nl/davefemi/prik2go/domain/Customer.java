package nl.davefemi.prik2go.domain;

import java.util.ArrayList;
import java.util.List;

public class Customer implements Comparable<Customer>{
    private long number;
    private List<String> closestBranches = new ArrayList<>();

    public Customer(long number) {
        this.number = number;
    }

    public void setClosestBranches(List<String> locations) {
        closestBranches = locations;
    }

    public long getNumber(){
        return number;
    }

    public List<String> getClosestLocations(){
        List<String> locations = new ArrayList<String>();
        closestBranches.forEach(locations::add);
        return locations;
    }
    /**
     * Comparator methode voor deze klasse op basis van Klantnummer
     */
    @Override
    public int compareTo(Customer c) {
        return Integer.compare((int) this.number, (int) c.number);
    }
}
