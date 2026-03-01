package nl.davefemi.prik2go.service.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.davefemi.prik2go.data.dto.domain.BranchDTO;
import nl.davefemi.prik2go.data.mapper.domain.BranchMapper;
import nl.davefemi.prik2go.data.repository.domain.BranchRepository;
import nl.davefemi.prik2go.domain.Branch;
import nl.davefemi.prik2go.domain.Customer;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class DomainService {
    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    private Map<String, Branch> branches = new TreeMap<>();

    /**
     * Verzorgt de initialisatie van de Vestigings-arraylist.
     * @throws ApplicatieException
     */
    @PostConstruct
    private void init() {
        branchRepository.findAll().forEach(b ->
                branches.put(b.getName(), branchMapper.mapEntityToDomain(b)));
        branches.forEach((n, b) ->
                log.info(b.getInitialCustomers().size()+ " Customers for [{}] have been retrieved", n));
    }

    /**
     * Geeft een lijst met de vestigingslocaties terug.
     * @return List<String> plaatsnamen
     */
    public List<String> getLocations() {
        List<String> locations = new ArrayList<String>();
        branches.keySet().forEach(locations::add);
        return locations;
    }


    /**
     * Geeft de KlantenDTO voor de gegeven locatie terug. Als er geen locatie gekozen is, of de
     * terugkeerwaarde van de dto is null, wordt een exceptie opgegooid.
     * @param location van de vestiging
     * @return KlantenDTO met klantgegevens voor de gegeven locatie
     * @throws ApplicatieException
     */
    public BranchDTO getBranchDTO(String location) throws ApplicatieException {
        Branch branch;
        if (location != null) {
            branch = branches.get(location);
            if (branch != null){
                return branchMapper.mapDomainToDTO(branch);
            }
        }
        throw new ApplicatieException ("Invalid branch");
    }

    /**
     * Geeft de status van de vestiging terug Als de vestiging open is, zal de status 'true'
     * zijn. Als een vestiging gesloten is, zal de status 'false' zijn.
     * @param location
     * @return boolean
     */
    public boolean getBranchStatus(String location) throws VestigingException {
        if (!branches.containsKey(location)){
            throw new VestigingException("Branch does not exist");
        }
        return branches.get(location).isOpen();
    }

    /**
     * Methode zal aan de hand van de huidige vestigingsstatus kiezen tussen een sluiting
     * of heropening van de gegeven locatie.
     * @param location
     * @throws ApplicatieException
     * @throws VestigingException als alle vestigingen status 'gesloten' bereiken wordt deze
     * exceptie opgegooid
     */
    public void changeBranchStatus(String location) throws VestigingException{
        if (!branches.containsKey(location)){
            throw new VestigingException("Branch does not exist");
        }
        if(branches.get(location).isOpen()) {
            AtomicInteger amountOfOpenBranches = new AtomicInteger();
            branches.values().forEach(b -> {if(b.isOpen()){
                amountOfOpenBranches.getAndIncrement();
            }});
            if(amountOfOpenBranches.get() > 1) {
                closeBranch(location);}
            else {
                log.warn("Cannot close [{}]. Closing all locations is not allowed", location);
                throw new VestigingException("Er moet minstens 1 vestiging open blijven");
            }
        }
        else {
            openBranch(location);
        }
    }

    /**
     * Methode om een Vestiging te sluiten en de bijbehorende klanten te plaatsen bij een andere
     * vestiging. Per huidige Klant van de gegeven vestiging wordt de interne lijst van dichtstbijzijnde
     * vestigingen opgevraagd. Voor deze vestigingen geldt dat de eerste die open is en niet gelijk is
     * aan de huidige vestiging, de nieuwe vestiging wordt. Als er geen vestigingen open zijn,
     * wordt er geen vestiging toegewezen. De vestiging wordt gesloten.
     * @param location
     */
    private void closeBranch(String location) {
        Branch branch = branches.get(location);
        for (Customer c : branch.getCurrentCustomers()) {
            for (String b: c.getClosestLocations()) {
                if (branches.get(b).isOpen() && !branches.get(b).equals(branch)) {
                    branches.get(b).addCustomer(c);
                    break;
                }
            }
        }
        branch.setClosed();
    }

    /**
     * Methode om een Vestiging te heropenen en de bijbehorende oorspronkelijke klanten te verwijderen bij
     * de overige vestigingen, voor zover die daar te vinden zijn. De Vestiging zal zijn eigen oorspronkelijke
     * klanten weer instellen als huidige klanten.
     * @param location
     */
    private void openBranch(String location) {
        Branch branch = branches.get(location);
        synchronized (this) {
            for (Customer c : branch.getInitialCustomers()) {
                for (Branch b : branches.values()) {
                    if (b.hasCustomer(c) && !b.equals(branch)) {
                        b.removeCustomer(c);
                    }
                }
            }
            branch.setOpen();
        }
    }
}
