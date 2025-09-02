package nl.davefemi.prik2go.data.repository;

import nl.davefemi.prik2go.data.entity.OAuthClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthClientRepository extends JpaRepository <OAuthClientEntity, Long> {

}
