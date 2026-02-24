package nl.davefemi.prik2go.service.auth;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.authorization.PasswordManager;
import nl.davefemi.prik2go.authorization.SessionFactory;
import nl.davefemi.prik2go.data.dto.UserAccountDTO;
import nl.davefemi.prik2go.data.dto.SessionResponseDTO;
import nl.davefemi.prik2go.data.dto.UserSessionDTO;
import nl.davefemi.prik2go.data.entity.UserAccountEntity;
import nl.davefemi.prik2go.data.entity.UserSessionEntity;
import nl.davefemi.prik2go.data.mapper.UserAccountMapper;
import nl.davefemi.prik2go.data.mapper.UserSessionMapper;
import nl.davefemi.prik2go.data.repository.UserAccountRepository;
import nl.davefemi.prik2go.data.repository.UserSessionRepository;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Qualifier("defaultAuth")
public class AuthService implements AuthServiceInterface {
    private final SessionFactory sessionFactory;
    private final UserAccountMapper userAccountMapper;
    private final UserAccountRepository userAccountRepository;
    private final UserSessionMapper userSessionMapper;
    private final UserSessionRepository userSessionRepository;
    @PersistenceContext
    private final EntityManager manager;
    private final PasswordManager passwordManager;

    @Transactional
    @Override
    public SessionResponseDTO createUser(UserAccountDTO credentials) {
        if (!userAccountRepository.existsByEmail(credentials.getEmail())){
            credentials.setPassword(passwordManager.hashPassword(credentials.getPassword()));
            UserAccountEntity entity = userAccountRepository.save(userAccountMapper.mapToEntity(credentials));
            manager.refresh(entity);
            return createSession(userAccountMapper.mapToDTO(entity));
        }
        return null;
    }

    @Override
    public SessionResponseDTO changePassword(UserAccountDTO credentials) throws ApplicatieException, IllegalArgumentException {
        UserAccountEntity entity = userAccountRepository.findByUserid(credentials.getUser());
        if (entity != null) {
            if (!passwordManager.match(credentials.getPassword(), entity.getPassword())){
                throw new IllegalArgumentException("Password is incorrect");
            }
            if(passwordManager.match(credentials.getNewPassword(), entity.getPassword())){
                throw new IllegalArgumentException("New password cannot be identical to old");
            }
                entity.setPassword(passwordManager.hashPassword(credentials.getNewPassword()));
                userAccountRepository.save(entity);
                return createSession(userAccountMapper.mapToDTO(entity));
        }
        throw new ApplicatieException("Unable to change password");
    }

    /*
    TODO: make sure that all authenticated requests validate session prior to execution.
     */

    @Override
    public SessionResponseDTO validateUser(UserAccountDTO credentials) throws IllegalAccessException, IllegalArgumentException {
        UserAccountDTO user = retrieveUser(credentials.getEmail());
        if (passwordManager.match(credentials.getPassword(), user.getPassword())){
            return createSession(user);
        }
        throw new IllegalAccessException("Incorrect password");
    }

    @Override
    public SessionResponseDTO createSession(UserAccountDTO user) {
        UserSessionDTO session = sessionFactory.generateSession(user);
        userSessionRepository.deleteByUUID(user.getUser());
        UserSessionEntity entity = userSessionRepository.save(userSessionMapper.mapToEntity(session, userAccountRepository.getReferenceById(user.getId())));
        return userSessionMapper.mapToResponseDTO(entity);
    }

    @Override
    public boolean validateSession(UUID user, UUID tokenId){
        return (userSessionRepository.existsByTokenId(user, tokenId));
    }

    @Override
    public boolean endSession(SessionResponseDTO session) {
        userSessionRepository.deleteByUUID(session.getUser());
        return false;
    }

    private UserAccountDTO retrieveUser(String email) throws IllegalArgumentException{
        try {
            return userAccountMapper.mapToDTO(userAccountRepository.findByEmail(email));
        } catch (Exception e) {
            throw new IllegalArgumentException("User not found");
        }
    }
}
