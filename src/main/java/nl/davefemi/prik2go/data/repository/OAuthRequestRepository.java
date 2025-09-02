package nl.davefemi.prik2go.data.repository;

import nl.davefemi.prik2go.data.entity.OAuthRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OAuthRequestRepository extends JpaRepository<OAuthRequestEntity, UUID> {
}
