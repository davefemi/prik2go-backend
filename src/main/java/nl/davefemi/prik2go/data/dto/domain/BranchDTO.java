package nl.davefemi.prik2go.data.dto.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object voor klantgegevens van een vestiging
 * Alle klantnummers worden in een lijst opgenomen en het aantal 
 * klanten opgeslagen.
 */
@Data
public class BranchDTO {
        private List<Long> customerIds = new ArrayList<>();
        private int numberOfCustomers = customerIds.size();

}
