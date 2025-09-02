package nl.davefemi.prik2go.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.PollingResponseDTO;
import nl.davefemi.prik2go.data.dto.SessionResponseDTO;
import nl.davefemi.prik2go.data.entity.OAuthUserAccountEntity;
import nl.davefemi.prik2go.data.entity.UserAccountEntity;
import nl.davefemi.prik2go.data.mapper.UserAccountMapper;
import nl.davefemi.prik2go.data.repository.OAuthClientRepository;
import nl.davefemi.prik2go.data.repository.OAuthUserAccountRepository;
import nl.davefemi.prik2go.data.repository.UserAccountRepository;
import nl.davefemi.prik2go.exceptions.AuthorizationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    public final OAuthUserAccountRepository oAuthUserAccountRepository;
    public final AuthService authService;
    public final UserAccountMapper userAccountMapper;
    private final UserAccountRepository userAccountRepository;
    private final OAuthClientRepository oAuthClientRepository;
    @PersistenceContext
    private final EntityManager manager;

    @Transactional
    public SessionResponseDTO validateOidcUser(OidcUser user, String userId) throws AuthorizationException {
        OAuthUserAccountEntity entity = oAuthUserAccountRepository.findOAuthUserAccountEntityByEmail(user.getEmail());
        if (entity != null) {
            return authService.createSession(userAccountMapper.mapToDTO(entity.getUserAccount()));
        }
        if (userId != null){
            return linkOidcUser(user, userId);
        }
        throw new AuthorizationException("Account doesn't exist");
    }

    @Transactional
    public SessionResponseDTO linkOidcUser(OidcUser user, String userId) throws AuthorizationException{
        try {
            OAuthUserAccountEntity entity = new OAuthUserAccountEntity();
            entity.setClient(oAuthClientRepository.getReferenceById((long) 1));
            entity.setEmail(user.getEmail());
            entity.setUserAccount(userAccountRepository.findByUserid(UUID.fromString(userId)));
            oAuthUserAccountRepository.save(entity);
            manager.refresh(entity);
            return authService.createSession(userAccountMapper.mapToDTO(userAccountRepository.findByUserid(UUID.fromString(userId))));
        }
        catch (Exception e){
            throw new AuthorizationException(e.getMessage());
        }
    }

    public PollingResponseDTO getDevicePolling(){
        return new PollingResponseDTO();
    }
}
