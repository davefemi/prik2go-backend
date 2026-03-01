package nl.davefemi.prik2go.service.auth;

import nl.davefemi.prik2go.data.dto.identity.UserAccountDTO;
import nl.davefemi.prik2go.data.dto.identity.SessionResponseDTO;
import nl.davefemi.prik2go.controller.exceptions.Prik2GoException;

import java.util.UUID;

public interface AuthServiceInterface {

    SessionResponseDTO createUser(UserAccountDTO credentials);

    SessionResponseDTO validateUser(UserAccountDTO credentials) throws IllegalAccessException, IllegalArgumentException;

    SessionResponseDTO createSession(UserAccountDTO credentials);

    boolean validateSession(UUID user, UUID tokenId);

    SessionResponseDTO changePassword(UserAccountDTO credentials) throws Prik2GoException, IllegalArgumentException;

    boolean endSession(SessionResponseDTO session);
}
