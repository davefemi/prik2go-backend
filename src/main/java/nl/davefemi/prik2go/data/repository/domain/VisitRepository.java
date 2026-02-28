package nl.davefemi.prik2go.data.repository.domain;

import nl.davefemi.prik2go.data.entity.domain.BranchEntity;
import nl.davefemi.prik2go.data.entity.domain.CustomerEntity;
import nl.davefemi.prik2go.data.entity.domain.VisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitRepository extends JpaRepository <VisitEntity, Long> {


}
