package nl.davefemi.prik2go.data.repository.domain;

import nl.davefemi.prik2go.data.entity.domain.BranchEntity;
import nl.davefemi.prik2go.data.entity.domain.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    @Query("SELECT          DISTINCT B.customer " +
            "FROM            VisitEntity B " +
            "WHERE           :branch IN  " +
            "(SELECT        BZ.branch " +
            "FROM           VisitEntity BZ " +
                            "JOIN CustomerEntity K ON BZ.customer.nr = K.nr " +
                            "JOIN PostcodeInfoEntity KP ON K.postcode.postcode = KP.postcode " +
                            "JOIN BranchEntity V ON BZ.branch.location = V.location " +
                            "JOIN PostcodeInfoEntity VP ON V.postcode.postcode = VP.postcode " +
            "WHERE           BZ.customer = B.customer " +
            "ORDER BY        SQRT(POWER(KP.lat-VP.lat,2) + POWER(KP.lng-VP.lng,2)) ASC " +
            "LIMIT            1)")
    List<CustomerEntity> getCustomersByBranch(@Param("branch") BranchEntity branch);

}
