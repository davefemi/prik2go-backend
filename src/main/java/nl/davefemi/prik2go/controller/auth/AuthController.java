package nl.davefemi.prik2go.controller.auth;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.UserAccountDTO;
import nl.davefemi.prik2go.data.dto.SessionResponseDTO;
import nl.davefemi.prik2go.service.auth.AuthServiceInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    @Qualifier("defaultAuth")
    private final AuthServiceInterface service;

    @PostMapping("create-user")
    public ResponseEntity<?> createUser(@RequestBody UserAccountDTO credentials){
        SessionResponseDTO token = service.createUser(credentials);
        return ResponseEntity.ok(token);
    }

    @PostMapping("login")
    public ResponseEntity<?> loginUser(@RequestBody UserAccountDTO credentials) {
        try{
            SessionResponseDTO dto = service.validateUser(credentials);
            logger.info("Login successful for [" + dto.getUser() +"]");
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody UserAccountDTO credentials){
        SessionResponseDTO token = null;
        try {
            token = service.changePassword(credentials);
            logger.info("Password successfully changed for [" + credentials.getUser() + "]");
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            logger.warning(e.getMessage() + " for [" + credentials.getUser() +"]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("logout")
    public ResponseEntity<?> logoutUser(@RequestBody SessionResponseDTO session) {
        try{
            return ResponseEntity.ok(service.endSession(session));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
