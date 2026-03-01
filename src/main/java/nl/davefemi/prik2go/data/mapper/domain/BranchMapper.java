package nl.davefemi.prik2go.data.mapper.domain;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.domain.BranchDTO;
import nl.davefemi.prik2go.data.entity.domain.BranchEntity;
import nl.davefemi.prik2go.data.entity.domain.CustomerEntity;
import nl.davefemi.prik2go.data.repository.domain.CustomerRepository;
import nl.davefemi.prik2go.domain.Branch;
import nl.davefemi.prik2go.domain.Customer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BranchMapper {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public Branch mapEntityToDomain(BranchEntity branchEntity){
        List<Customer> customers = new ArrayList<>();
        for (CustomerEntity customer: customerRepository.getCustomersByBranch(branchEntity)){
            Customer c = customerMapper.mapEntityToDomain(customer);
            customers.add(c);
        }
        Branch branch = new Branch(branchEntity.getName(), customers);

        return branch;
    }

    public BranchDTO mapDomainToDTO(Branch branch){
        BranchDTO dto = new BranchDTO();
        List<Customer> customers = branch.getCurrentCustomers();
        for (Customer c : customers){
            dto.getCustomerIds().add(c.getNumber());
        }
        Collections.sort(dto.getCustomerIds());
        dto.setNumberOfCustomers(customers.size());
        return dto;
    }
}
