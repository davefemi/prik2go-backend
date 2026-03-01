package nl.davefemi.prik2go.domain;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Branch implements Comparable<Branch>{
    private String location;
    private boolean open = true;
    private List<Customer> initialCustomers = new ArrayList<>();
    private List<Customer> currentCustomers = new ArrayList<>();

    public Branch(String location, List<Customer> initialCustomers){
        this.location = location;
        this.initialCustomers = initialCustomers;
        initialCustomers.forEach(currentCustomers::add);
    }

    public String getLocation(){
        return location;
    }

    public synchronized void addCustomer(Customer customer) {
        if (currentCustomers != null ) {
            currentCustomers.add(customer);
        }
    }

    /**
     * Methode waarmee een Klant-object wordt verwijderd als huidige klant. De oorspronkelijke
     * klanten blijven ongewijzigd.
     * @param customer
     */
    public synchronized void removeCustomer(Customer customer) {
        if (currentCustomers != null) {
            currentCustomers.remove(customer);
        }
    }

    public synchronized List<Customer> getCurrentCustomers(){
        List<Customer> customers = new ArrayList<>();
        currentCustomers.forEach(customers::add);
        return customers;
    }

    public synchronized boolean hasCustomer(Customer c) {
        return currentCustomers.contains(c);
    }

    /**
     * Keert een lijst met oorspronkelijke Klant objecten terug
     * @return List<Klant> oorspronkelijke klanten
     */
    public synchronized List<Customer> getInitialCustomers(){
        List<Customer> customers = new ArrayList<>();
        initialCustomers.forEach(customers::add);
        return customers;
    }

    /**
     * Status van deze Vestiging wordt 'open' en oorspronkelijke klanten
     * worden toegevoegd aan aan de lijst voor huidige klanten.
     */
    public synchronized void setOpen() {
        if (!open) {
            open = true;
            if (initialCustomers != null) {
                initialCustomers.forEach(this::addCustomer);
                log.info("Branch [" + location + "] has been re-opened");
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
            log.info("Vestiging [" + location + "] is gesloten");
            if (currentCustomers!= null) {
                currentCustomers.clear();
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
     * Levert de locatie en aantal klanten van deze Vestiging als String waarde.
     */
    @Override
    public synchronized String toString(){
        return location + currentCustomers.size();
    }

    @Override
    public int compareTo(Branch b) {
        synchronized (this) {
            synchronized (b) {
                if (this.getLocation().equals(b.getLocation()) && this.currentCustomers != null
                        && b.currentCustomers != null) {
                    return ((Integer)this.currentCustomers.size()).compareTo((Integer) b.currentCustomers.size());
                }
            }
        }
        return this.location.compareTo(b.location);
    }
}
