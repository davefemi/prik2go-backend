package nl.davefemi.prik2go.data.repository.domain;

import nl.davefemi.prik2go.data.entity.domain.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, String> {

    @Query("SELECT          V.location " +
            "FROM            CustomerEntity K " +
            "JOIN PostcodeInfoEntity KP ON K.postcode.postcode = KP.postcode, " +
            "BranchEntity V " +
            "JOIN PostcodeInfoEntity VP ON V.postcode.postcode = VP.postcode " +
            "WHERE           K.nr = :customer_id " +
            "ORDER BY        SQRT(POWER(KP.lat-VP.lat,2) + POWER(KP.lng-VP.lng,2)) ASC")
    List<BranchEntity> getClosestBranches(@Param("customer_id") Long customerId);
}
