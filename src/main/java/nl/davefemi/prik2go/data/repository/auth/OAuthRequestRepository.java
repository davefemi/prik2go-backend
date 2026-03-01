package nl.davefemi.prik2go.data.repository.auth;

import nl.davefemi.prik2go.data.entity.auth.OAuthRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OAuthRequestRepository extends JpaRepository<OAuthRequestEntity, UUID> {

}
