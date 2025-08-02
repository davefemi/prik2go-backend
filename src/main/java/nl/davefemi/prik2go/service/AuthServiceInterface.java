package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.data.dto.UserAccountDTO;
import nl.davefemi.prik2go.data.dto.SessionResponseDTO;

public interface AuthServiceInterface {

    SessionResponseDTO createUser(UserAccountDTO credentials);

    SessionResponseDTO validateUser(UserAccountDTO credentials) throws IllegalAccessException, IllegalArgumentException;

    SessionResponseDTO createSession(UserAccountDTO credentials);

    void validateSession(String user);
}
