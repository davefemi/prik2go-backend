package nl.davefemi.prik2go.service.auth;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.authorization.EnvHelper;
import nl.davefemi.prik2go.authorization.PasswordManager;
import nl.davefemi.prik2go.authorization.SecretGenerator;
import nl.davefemi.prik2go.authorization.SessionFactory;
import nl.davefemi.prik2go.data.dto.*;
import nl.davefemi.prik2go.data.entity.OAuthRequestEntity;
import nl.davefemi.prik2go.data.entity.OAuthUserAccountEntity;
import nl.davefemi.prik2go.data.entity.UserAccountEntity;
import nl.davefemi.prik2go.data.entity.UserSessionEntity;
import nl.davefemi.prik2go.data.mapper.OAuthRequestMapper;
import nl.davefemi.prik2go.data.mapper.UserAccountMapper;
import nl.davefemi.prik2go.data.mapper.UserSessionMapper;
import nl.davefemi.prik2go.data.repository.*;
import nl.davefemi.prik2go.exceptions.AuthorizationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final SessionFactory sessionFactory;
    private final OAuthUserAccountRepository oAuthUserAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final UserAccountRepository userAccountRepository;
    private final OAuthClientRepository oAuthClientRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserSessionMapper userSessionMapper;
    @PersistenceContext
    private final EntityManager manager;
    private final OAuthRequestRepository oAuthRequestRepository;
    private final OAuthRequestMapper oAuthRequestMapper;
    private final PasswordManager passwordManager;
    private final EnvHelper envHelper;

    @Transactional
    public void validateOidcUser(String issuer, OidcUser user, String userId, String requestId) throws AuthorizationException, TimeoutException {
        if (validateRequest(requestId)) {
            if (userId == null) {
                loginUser(issuer, user, requestId);
            }
            else {
                linkOidcUser(issuer, user, userId, requestId);
            }
        }
    }

    @Transactional
    public void loginUser(String issuer,OidcUser user, String requestId) throws AuthorizationException {
        OAuthUserAccountEntity entity = oAuthUserAccountRepository.findOAuthUserAccountEntityByEmail(user.getEmail());
        if (entity != null) {
            try {
                OAuthRequestEntity requestEntity = oAuthRequestRepository.findById(UUID.fromString(requestId)).get();
                if (!requestEntity.getAuthorized()) {
                    createSession(userAccountMapper.mapToDTO(entity.getUserAccount()), requestEntity);
                    requestEntity.setAuthorized(true);
                    oAuthRequestRepository.save(requestEntity);
                }
            } catch (Exception e) {
                oAuthRequestRepository.deleteById(UUID.fromString(requestId));
                throw new AuthorizationException("This " + issuer + " account has not been linked yet");
            }
        }
        else {
            oAuthRequestRepository.deleteById(UUID.fromString(requestId));
            throw new AuthorizationException("This "+ issuer+" account has not been linked yet");
        }
    }

    private OAuthUserAccountEntity createOauthUserAccount(String issuer,OidcUser oidcUser, String userId){
        OAuthUserAccountEntity oAuthUserAccount = new OAuthUserAccountEntity();
        oAuthUserAccount.setClient(oAuthClientRepository.getOauthClientEntityByName(issuer));
        oAuthUserAccount.setEmail(oidcUser.getEmail());
        oAuthUserAccount.setUserAccount(userAccountRepository.findByUserid(UUID.fromString(userId)));
        return oAuthUserAccount;
    }

    @Transactional
    public void linkOidcUser(String issuer, OidcUser oidcUser, String userId, String requestId) throws AuthorizationException{
        try {
            OAuthUserAccountEntity oAuthUserAccount = oAuthUserAccountRepository.findOAuthUserAccountEntityByEmail(oidcUser.getEmail());
            if (oAuthUserAccount != null){
                if (oAuthUserAccount.getUserAccount().getUserid().toString().equals(userId)){
                    throw new AuthorizationException("This" + issuer + " account is already linked to this user account");
                }
            }
            UserAccountEntity userAccount = userAccountRepository.findByUserid(UUID.fromString(userId));
            if (oAuthUserAccountRepository.existsByUserAccountEntity(userAccount)){
                oAuthRequestRepository.deleteById(UUID.fromString(requestId));
                throw new AuthorizationException("A " + issuer + " account is already linked to this user account");
            }
            oAuthUserAccountRepository.save(createOauthUserAccount(issuer, oidcUser, userId));
            oAuthRequestRepository.findById(UUID.fromString(requestId)).get().setAuthorized(true);
        }
        catch (Exception e){
            throw new AuthorizationException(e.getMessage());
        }
    }

    @Transactional
    public SessionResponseDTO createSession(UserAccountDTO user, OAuthRequestEntity request) {
        UserSessionDTO session = sessionFactory.generateSession(user);
        userSessionRepository.deleteByUUID(user.getUser());
        UserSessionEntity entity = userSessionRepository.save(
                userSessionMapper.mapToEntity(session,
                        userAccountRepository.getReferenceById(user.getId())));
        manager.refresh(entity);
        request.setUserSession(entity);
        return userSessionMapper.mapToResponseDTO(entity);
    }

    @Transactional
    public SessionResponseDTO getSession(RequestDTO request) throws AuthorizationException {
        boolean auth;
        try {
            auth = isUserAuthenticated(request);
        }
        catch (Exception e){
            throw new AuthorizationException(e.getMessage());
        }
        if (auth){
            SessionResponseDTO dto = userSessionMapper
                    .mapToResponseDTO(
                            oAuthRequestRepository
                                    .getReferenceById(request.getRequestCode())
                                    .getUserSession());
            oAuthRequestRepository.deleteById(request.getRequestCode());
            return dto;
        }
        return null;
    }

    private OAuthResponseDTO generateOauthRequest(String userId){
        OAuthResponseDTO oauthRequest =  new OAuthResponseDTO();
        oauthRequest.setRequestCode(UUID.randomUUID());
        oauthRequest.setSecret(SecretGenerator.generateSecret(48));
        oauthRequest.setPollingInterval(2000L);
        oauthRequest.setExpiresAt(Instant.now().plusSeconds(300));
        oauthRequest.setUrl(String.format(envHelper.getBaseUrl() + "/oauth2/login/google?state=%s&uid=%s", oauthRequest.getRequestCode(), userId));
        return oauthRequest;
    }

    public OAuthResponseDTO getRequestID(String userId){
        OAuthResponseDTO polling =  generateOauthRequest(userId);
        oAuthRequestRepository.save(
                oAuthRequestMapper.mapToEntity(
                        polling,
                        passwordManager.hashPassword(polling.getSecret()
                        ),
                        false
                )
        );
        return polling;
    }

    public boolean isUserAuthenticated(RequestDTO request) throws AuthorizationException, TimeoutException {
        OAuthRequestEntity oAuthRequestEntity;
        try {
            oAuthRequestEntity = oAuthRequestRepository.getReferenceById(request.getRequestCode());
            if (oAuthRequestEntity != null) {
                if (!passwordManager.match(request.getSecret(), oAuthRequestEntity.getSecret()))
                    throw new AuthorizationException("Secret invalid");
                if (oAuthRequestEntity.getExpiresAt().isBefore(Instant.now())) {
                    oAuthRequestRepository.deleteById(request.getRequestCode());
                    throw new TimeoutException("Request time-out");
                }
                if (oAuthRequestEntity.getAuthorized())
                    return true;
            }
            return false;
        } catch (Exception e) {
            throw new AuthorizationException("Request could not be authenticated");
        }
    }

    public boolean validateRequest(String requestId) throws AuthorizationException, TimeoutException {
        OAuthRequestEntity oAuthRequestEntity = null;
        try {
            oAuthRequestEntity = oAuthRequestRepository.getReferenceById(UUID.fromString(requestId));
        } catch (Exception e) {
            throw new AuthorizationException("Request could not be authenticated");
        }
        if (oAuthRequestEntity != null) {
            if (oAuthRequestEntity.getExpiresAt().isBefore(Instant.now())) {
                oAuthRequestRepository.deleteById(UUID.fromString(requestId));
                throw new TimeoutException("Request time-out");
            }
            return true;
        }
        return false;
    }
}
