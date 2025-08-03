package nl.davefemi.prik2go.data.repository;

import jakarta.transaction.Transactional;
import nl.davefemi.prik2go.data.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSessionEntity u WHERE u.userId.user = :userId")
    void deleteByUUID(@Param("userId") UUID userId);
}
