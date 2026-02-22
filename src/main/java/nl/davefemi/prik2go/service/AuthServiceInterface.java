package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.data.dto.UserAccountDTO;
import nl.davefemi.prik2go.data.dto.SessionResponseDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;

import java.util.UUID;

public interface AuthServiceInterface {

    SessionResponseDTO createUser(UserAccountDTO credentials);

    SessionResponseDTO validateUser(UserAccountDTO credentials) throws IllegalAccessException, IllegalArgumentException;

    SessionResponseDTO createSession(UserAccountDTO credentials);

    boolean validateSession(UUID user, UUID tokenId);

    SessionResponseDTO changePassword(UserAccountDTO credentials) throws ApplicatieException, IllegalArgumentException;

    boolean endSession(SessionResponseDTO session);
}
