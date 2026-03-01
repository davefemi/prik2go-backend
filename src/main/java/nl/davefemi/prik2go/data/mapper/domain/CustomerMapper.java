package nl.davefemi.prik2go.data.mapper.domain;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.domain.BranchDTO;
import nl.davefemi.prik2go.data.entity.domain.BranchEntity;
import nl.davefemi.prik2go.data.entity.domain.CustomerEntity;
import nl.davefemi.prik2go.data.repository.domain.BranchRepository;
import nl.davefemi.prik2go.domain.Customer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerMapper {
    private final BranchRepository branchRepository;

    public Customer mapEntityToDomain(CustomerEntity customerEntity){
        Customer customer = new Customer(customerEntity.getNr());
        List<BranchEntity> branches = branchRepository.getClosestBranches(customerEntity.getNr());
        List<String> locations = new ArrayList<>();
        synchronized (this) {
            for (BranchEntity b : branches) {

                locations.add((b.getName()));
            }
            customer.setClosestBranches(locations);
        }
        return customer;
    }
}
