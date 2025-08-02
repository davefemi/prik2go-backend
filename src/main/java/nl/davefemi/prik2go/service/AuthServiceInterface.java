package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.data.dto.UserAccountDTO;

public interface AuthServiceInterface {

    String createUser(UserAccountDTO credentials);

    String validateUser(UserAccountDTO credentials);

    String createSession(UserAccountDTO credentials);
}
