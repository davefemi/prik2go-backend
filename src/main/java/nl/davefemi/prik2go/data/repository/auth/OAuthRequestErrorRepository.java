package nl.davefemi.prik2go.data.repository.auth;

import jakarta.transaction.Transactional;
import nl.davefemi.prik2go.data.entity.auth.OAuthRequestEntity;
import nl.davefemi.prik2go.data.entity.auth.OAuthRequestErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OAuthRequestErrorRepository extends JpaRepository <OAuthRequestErrorEntity, Long> {

    @Transactional
    @Query("SELECT r FROM OAuthRequestErrorEntity r WHERE r.requestId= :requestId")
    OAuthRequestErrorEntity findByRequestId(@Param("requestId") OAuthRequestEntity requestId);
}
