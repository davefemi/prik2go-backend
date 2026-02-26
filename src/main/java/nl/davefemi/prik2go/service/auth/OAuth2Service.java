package nl.davefemi.prik2go.service.auth;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.davefemi.prik2go.authorization.PasswordManager;
import nl.davefemi.prik2go.authorization.SecretGenerator;
import nl.davefemi.prik2go.authorization.SessionFactory;
import nl.davefemi.prik2go.data.dto.*;
import nl.davefemi.prik2go.data.entity.*;
import nl.davefemi.prik2go.data.mapper.OAuthRequestMapper;
import nl.davefemi.prik2go.data.mapper.UserAccountMapper;
import nl.davefemi.prik2go.data.mapper.UserSessionMapper;
import nl.davefemi.prik2go.data.repository.*;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.AuthorizationException;
import nl.davefemi.prik2go.service.auth.oauth2client.OAuth2ClientRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final SessionFactory sessionFactory;
    private final OAuthUserAccountRepository oAuthUserAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final UserAccountRepository userAccountRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserSessionMapper userSessionMapper;
    @PersistenceContext
    private final EntityManager manager;
    private final OAuthRequestRepository oAuthRequestRepository;
    private final OAuthRequestMapper oAuthRequestMapper;
    private final PasswordManager passwordManager;
    private final OAuth2ClientRegistry registry;
    private final OAuthRequestErrorRepository oAuthRequestErrorRepository;


    @Transactional
    public void validateOidcUser(String provider, OidcUser user, String userId, String requestId) throws AuthorizationException, TimeoutException {
        if (validateRequest(requestId)) {
            if (userId == null) {
                loginUser(provider, user, requestId);
            }
            else {
                linkOidcUser(provider, user, userId, requestId);
            }
        }
    }

    @Transactional
    public void loginUser(String issuer,OidcUser user, String requestId) throws AuthorizationException {
        OAuthUserAccountEntity entity = oAuthUserAccountRepository.findOAuthUserAccountEntityByEmail(user.getEmail());
        OAuthRequestEntity requestEntity = oAuthRequestRepository.findById(UUID.fromString(requestId)).get();
        if (entity != null) {
            try {
                if (!requestEntity.getAuthorized()) {
                    createSession(userAccountMapper.mapToDTO(entity.getUserAccount()), requestEntity);
                    requestEntity.setAuthorized(true);
                    oAuthRequestRepository.save(requestEntity);
                    log.info("[{} has been logged in successfully]", user.getEmail());
                }
            } catch (Exception e) {
                String error = "An error occurred while logging in this " + StringUtils.capitalize(issuer) + "account";
                log.info(error);
                createRequestError(requestEntity,error);
                throw new AuthorizationException(error);
            }
        }
        else {
            String error = "This " + StringUtils.capitalize(issuer) + " account has not been linked yet";
            log.info(error);
            createRequestError(requestEntity,error);
            throw new AuthorizationException(error);        }
    }


    private OAuthUserAccountEntity createOauthUserAccount(String provider,OidcUser oidcUser, String userId) throws ApplicatieException {
        OAuthUserAccountEntity oAuthUserAccount = new OAuthUserAccountEntity();
        oAuthUserAccount.setClient(registry.getOAuth2Client(provider).getOAuthClientEntity());
        oAuthUserAccount.setEmail(oidcUser.getEmail());
        oAuthUserAccount.setUserAccount(userAccountRepository.findByUserid(UUID.fromString(userId)));
        return oAuthUserAccount;
    }

    @Transactional
    public void linkOidcUser(String provider, OidcUser oidcUser, String userId, String requestId) throws AuthorizationException{
        OAuthRequestEntity oAuthRequestEntity = oAuthRequestRepository.getReferenceById(UUID.fromString(requestId));
            OAuthUserAccountEntity oAuthUserAccount = oAuthUserAccountRepository.findOAuthUserAccountEntityByEmail(oidcUser.getEmail());
            if (oAuthUserAccount != null) {
                if (oAuthUserAccount.getUserAccount().getUserid().toString().equals(userId)) {
                    String error = StringUtils.capitalize(oidcUser.getEmail()) + " is already linked to this user account";
                    log.info(error);
                    createRequestError(oAuthRequestEntity, error);
                    throw new AuthorizationException(error);
                }
            }
            UserAccountEntity userAccount = userAccountRepository.findByUserid(UUID.fromString(userId));
            if (oAuthUserAccountRepository.existsByUserAccountEntity(userAccount)) {
                String error =  " An OAuth account is already associated with this user account";
                log.info(error);
                createRequestError(oAuthRequestEntity, error);
                throw new AuthorizationException(error);
            }
        try {
            oAuthUserAccountRepository.save(createOauthUserAccount(provider, oidcUser, userId));
            oAuthRequestRepository.findById(UUID.fromString(requestId)).get().setAuthorized(true);
            log.info("[{} has been linked successfully]", oidcUser.getEmail());
        }
        catch (Exception e){
            throw new AuthorizationException(e.getMessage());
        }
    }

    @Transactional
    public void unlinkOidcUser(String userId){
        UserAccountEntity userAccount = userAccountRepository.findByUserid(UUID.fromString(userId));
        if (userAccount == null){
            throw new OpenApiResourceNotFoundException("User does not exist");
        }
        OAuthUserAccountEntity oAuthUserAccount = oAuthUserAccountRepository.findOAuthUserAccountEntityByUserAccount(userAccount);
        if (oAuthUserAccount == null){
            throw new OpenApiResourceNotFoundException(("There is no Oauth-account associated with this user account"));
        }
        oAuthUserAccountRepository.delete(oAuthUserAccount);
    }

    @Transactional
    public void createSession(UserAccountDTO user, OAuthRequestEntity request) {
        UserSessionDTO session = sessionFactory.generateSession(user);
        userSessionRepository.deleteByUUID(user.getUser());
        UserSessionEntity entity = userSessionRepository.save(
                userSessionMapper.mapToEntity(session,
                        userAccountRepository.getReferenceById(user.getId())));
        manager.refresh(entity);
        request.setUserSession(entity);
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

    private OAuthResponseDTO generateOauthRequest(String userId, String provider) throws ApplicatieException {
        OAuthResponseDTO oauthRequest =  new OAuthResponseDTO();
        oauthRequest.setRequestCode(UUID.randomUUID());
        oauthRequest.setProvider(registry.getOAuth2Client(provider).getProviderName()); //check for format
        oauthRequest.setSecret(SecretGenerator.generateSecret(48));
        oauthRequest.setPollingInterval(2000L);
        oauthRequest.setExpiresAt(Instant.now().plusSeconds(300));
        oauthRequest.setUrl(String.format(registry.getOAuth2Client(provider).getClientURL()+"&state=%s&uid=%s", oauthRequest.getRequestCode(), userId));
        return oauthRequest;
    }

    public OAuthResponseDTO getRequestID(String userId, String provider) throws ApplicatieException {
        OAuthResponseDTO polling =  generateOauthRequest(userId, provider);
        try {
            oAuthRequestRepository.save(
                    oAuthRequestMapper.mapToEntity(
                            polling,
                            passwordManager.hashPassword(polling.getSecret()
                            ),
                            false, registry.getOAuth2Client(provider).getOAuthClientEntity()
                    )
            );
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
        return polling;
    }

    public boolean isUserAuthenticated(RequestDTO request) throws AuthorizationException, ApplicatieException, TimeoutException {
        OAuthRequestEntity oAuthRequestEntity = oAuthRequestRepository.getReferenceById(request.getRequestCode());
        if (oAuthRequestEntity != null) {
            if (!passwordManager.match(request.getSecret(), oAuthRequestEntity.getSecret()))
                throw new AuthorizationException("Secret invalid");
            if (oAuthRequestEntity.getExpiresAt().isBefore(Instant.now())) {
                oAuthRequestRepository.deleteById(request.getRequestCode());
                throw new TimeoutException("Request time-out");
            }
            OAuthRequestErrorEntity error = oAuthRequestErrorRepository.findByRequestId(oAuthRequestEntity);
            if (error != null){
                oAuthRequestRepository.deleteById(oAuthRequestEntity.getRequestId());
                throw new ApplicatieException(error.getError());
            }
        }
        return oAuthRequestEntity.getAuthorized();
    }

    public boolean validateRequest(String requestId) throws AuthorizationException {
        try {
            OAuthRequestEntity oAuthRequestEntity = oAuthRequestRepository.getReferenceById(UUID.fromString(requestId));
            if (oAuthRequestEntity.getExpiresAt().isBefore(Instant.now())) {
                createRequestError(oAuthRequestEntity, "Request time-out");
                return false;
            }
        } catch (Exception e) {
            throw new AuthorizationException("Request could not be authenticated");
        }
        return true;
    }

    private void createRequestError (OAuthRequestEntity request, String message) {
        OAuthRequestErrorEntity error = new OAuthRequestErrorEntity();
        error.setRequestId(request);
        error.setError(message);
        oAuthRequestErrorRepository.save(error);
    }

}
