package nl.davefemi.prik2go.data.repository;

import jakarta.transaction.Transactional;
import nl.davefemi.prik2go.data.entity.OAuthUserAccountEntity;
import nl.davefemi.prik2go.data.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthUserAccountRepository extends JpaRepository<OAuthUserAccountEntity, Long> {

    @Transactional
    @Query("SELECT  o FROM OAuthUserAccountEntity o WHERE o.email = :email")
    OAuthUserAccountEntity findOAuthUserAccountEntityByEmail(@Param("email") String email);

    @Transactional
    @Query("SELECT count(*)>0 FROM OAuthUserAccountEntity o WHERE o.userAccount = :user_account")
    boolean existsByUserAccountEntity(@Param("user_account") UserAccountEntity userAccount);

    @Transactional
    @Query("Select o FROM OAuthUserAccountEntity  o WHERE o.userAccount =  :user_account")
    OAuthUserAccountEntity findOAuthUserAccountEntityByUserAccount(@Param("user_account") UserAccountEntity userAccount);
}
