package nl.davefemi.prik2go.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.authorization.PasswordManager;
import nl.davefemi.prik2go.authorization.SessionFactory;
import nl.davefemi.prik2go.data.dto.UserAccountDTO;
import nl.davefemi.prik2go.data.dto.UserSessionDTO;
import nl.davefemi.prik2go.data.entity.UserAccountEntity;
import nl.davefemi.prik2go.data.mapper.UserAccountMapper;
import nl.davefemi.prik2go.data.mapper.UserSessionMapper;
import nl.davefemi.prik2go.data.repository.UserAccountRepository;
import nl.davefemi.prik2go.data.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Qualifier("defaultAuth")
public class AuthService implements AuthServiceInterface{
    private static final Logger logger = Logger.getLogger(AuthServiceInterface.class.getName());
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
    public String createUser(UserAccountDTO credentials) {
        if (!userAccountRepository.existsByEmail(credentials.getEmail())){
            credentials.setPassword(passwordManager.hashPassword(credentials.getPassword()));
            UserAccountEntity entity = userAccountRepository.save(userAccountMapper.mapToEntity(credentials));
            manager.refresh(entity);
            return createSession(userAccountMapper.mapToDTO(entity));
        }
        return null;
    }

    @Override
    public String validateUser(UserAccountDTO credentials) {
        UserAccountDTO user = retrieveUser(credentials.getEmail());
        if (passwordManager.match(credentials.getPassword(), user.getPassword())){
            return createSession(user);
        }
        return null;
    }

    @Transactional
    @Override
    public String createSession(UserAccountDTO user) {
        UserSessionDTO session = sessionFactory.generateSession(user);
        userSessionRepository.save(userSessionMapper.mapToEntity(session, userAccountRepository.getReferenceById(user.getId())));
        return session.getToken();
    }


    private String retrieveSession(){
        return "";
    }

    private UserAccountDTO retrieveUser(String email){
        return userAccountMapper.mapToDTO(userAccountRepository.findByEmail(email));
    }
}
