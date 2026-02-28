package nl.davefemi.prik2go.data.mapper.domain;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.domain.BranchDTO;
import nl.davefemi.prik2go.data.entity.domain.BranchEntity;
import nl.davefemi.prik2go.data.entity.domain.CustomerEntity;
import nl.davefemi.prik2go.data.repository.domain.CustomerRepository;
import nl.davefemi.prik2go.domain.Branch;
import nl.davefemi.prik2go.domain.Customer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BranchMapper {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public Branch mapEntityToDomain(BranchEntity branchEntity){
        Branch branch = new Branch(branchEntity.getName());
        List<CustomerEntity> customers = customerRepository.getCustomersByBranch(branchEntity);
        for (CustomerEntity c : customers){
            Customer customer = customerMapper.mapEntityToDomain(c);
            branch.getInitialCustomers().add(customer);
            branch.getCurrentCustomers().add(customer);
        }
        return branch;
    }

    public BranchDTO mapDomainToDTO(Branch branch){
        BranchDTO dto = new BranchDTO();
        List<Customer> customers = branch.getCurrentCustomers();
        for (Customer c : customers){
            dto.getCustomerIds().add(c.getNumber());
        }
        Collections.sort(dto.getCustomerIds());
        return dto;
    }
}
