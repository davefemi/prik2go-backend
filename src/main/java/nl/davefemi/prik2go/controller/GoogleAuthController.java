package nl.davefemi.prik2go.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.RequestDTO;
import nl.davefemi.prik2go.exceptions.AuthorizationException;
import nl.davefemi.prik2go.service.OAuth2Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("oauth2/")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final OAuth2Service oAuth2Service;

    @GetMapping("login/google")
    public ResponseEntity<?> loginUser(@RequestParam ("state") String requestId, HttpServletRequest req) {
        try {
            if (!oAuth2Service.validateRequest(requestId))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Request denied");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        req.getSession(true).setAttribute("request", requestId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }

    @GetMapping("link/google")
    public ResponseEntity<Void> linkUser(HttpServletRequest req, Principal principal){
        req.getSession(true).setAttribute("userId", principal.getName());
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }

    @PostMapping("revoke")
    public ResponseEntity<?> unlinkUserAccount(){
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("status/google")
    public ResponseEntity<?> userLinked(@RequestParam("login") boolean result){
        return result
                ? ResponseEntity.status(HttpStatus.OK).body("Authentication successful")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to authenticate");
    }

    @GetMapping("request/start")
    public ResponseEntity<?> getRequest(){
        return ResponseEntity.of(Optional.of(oAuth2Service.getRequestID()));
    }

    @GetMapping("request/polling")
    public ResponseEntity<?> isAuthenticated(@RequestBody RequestDTO request) {
        boolean result;
        try {
            result = oAuth2Service.isUserAuthenticated(request);
        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(e.getMessage());
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        if (result)
        return ResponseEntity.status(HttpStatus.OK).body(true);
        return ResponseEntity.status(HttpStatus.OK).body(false);
    }

    @GetMapping("request/get-session")
    public ResponseEntity<?> getSession(@RequestBody RequestDTO request) {
        try {
            return ResponseEntity.of(Optional.of(oAuth2Service.getSession(request)));
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
