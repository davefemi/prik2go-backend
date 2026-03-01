package nl.davefemi.prik2go.data.repository.identity;

import jakarta.transaction.Transactional;
import nl.davefemi.prik2go.data.entity.identity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {

    @Transactional
    @Query("SELECT count(*)>0 FROM UserAccountEntity u WHERE u.email = :email ")
    boolean existsByEmail(@Param("email") String email);

    @Transactional
    @Query("SELECT u FROM UserAccountEntity u WHERE u.email =:email")
    UserAccountEntity findByEmail(@Param("email") String email);

    @Transactional
    @Query("SELECT u FROM UserAccountEntity u WHERE u.userid = :userid")
    UserAccountEntity findByUserid(@Param("userid") UUID userid);
}
