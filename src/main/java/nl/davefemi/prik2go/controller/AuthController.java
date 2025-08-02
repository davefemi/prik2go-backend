package nl.davefemi.prik2go.controller;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.UserAccountDTO;
import nl.davefemi.prik2go.data.dto.SessionResponseDTO;
import nl.davefemi.prik2go.service.AuthServiceInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/auth")
public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    @Qualifier("defaultAuth")
    private final AuthServiceInterface service;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody UserAccountDTO credentials){
        SessionResponseDTO token = service.createUser(credentials);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserAccountDTO credentials) {
        try{
            ResponseEntity<?> response = ResponseEntity.ok(service.validateUser(credentials));
            logger.info("Login successful for [" +credentials.getEmail() +"]");
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
