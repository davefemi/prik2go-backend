package nl.davefemi.prik2go.data.repository;

import jakarta.transaction.Transactional;
import nl.davefemi.prik2go.data.entity.OAuthRequestEntity;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OAuthRequestRepository extends JpaRepository<OAuthRequestEntity, UUID> {

//    @Transactional
//    @SQLUpdate(sql = "UPDATE OAuthRequestEntity o SET o.auth_failure = :failure WHERE o.request_id = :request_id")
//    void updateRequest(@Param("request_id") UUID requestId, boolean failure);
}
