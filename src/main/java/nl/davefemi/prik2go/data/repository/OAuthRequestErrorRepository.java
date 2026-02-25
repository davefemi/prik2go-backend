package nl.davefemi.prik2go.data.repository;

import nl.davefemi.prik2go.data.entity.OAuthRequestEntity;
import nl.davefemi.prik2go.data.entity.OAuthRequestErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OAuthRequestErrorRepository extends JpaRepository <OAuthRequestErrorEntity, Long> {

    @Query("SELECT r FROM OAuthRequestErrorEntity r WHERE r.requestId= :requestId")
    OAuthRequestErrorEntity findByRequestId(@Param("requestId") OAuthRequestEntity requestId);
}
