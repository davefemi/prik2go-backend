package nl.davefemi.prik2go.data.repository.auth;

import jakarta.transaction.Transactional;
import nl.davefemi.prik2go.data.entity.auth.OAuthClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthClientRepository extends JpaRepository <OAuthClientEntity, Long> {

    @Transactional
    @Query("SELECT c FROM OAuthClientEntity c WHERE lower(c.name) = lower(:name)")
    OAuthClientEntity getOauthClientEntityByName(@Param("name") String name);
}
