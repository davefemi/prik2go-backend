package nl.davefemi.prik2go.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
@RequiredArgsConstructor
public class GoogleAuthController {
    private final OAuth2Service oAuth2Service;

    @GetMapping("/oauth2/login/google")
    public ResponseEntity<?> loginUser(@RequestParam ("state") String requestId, @RequestParam("uid") String userId, HttpServletRequest req) {
        try {
            if (!oAuth2Service.validateRequest(requestId))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Request denied");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        HttpSession sess = req.getSession(true);
        sess.setAttribute("request", requestId);
        sess.setAttribute("userId", userId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }

    @GetMapping("/private/oauth2/request/start")
    public ResponseEntity<?> linkUser(HttpServletRequest req, Principal principal){
        // TODO check in repository if user is already linked to a Google account
        return ResponseEntity.of(Optional.of(oAuth2Service.getRequestID(principal.getName())));
    }

    @PostMapping("/oauth2/revoke")
    public ResponseEntity<?> unlinkUserAccount(){
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("/oauth2/status/google")
    public ResponseEntity<?> userLinked(@RequestParam("login") boolean status){
        String param = status
                ? "success"
                : "failure";
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/status/google/landing.html?status=" + param))
                .build();
    }

    @GetMapping("/oauth2/request/start")
    public ResponseEntity<?> getRequest(){
        return ResponseEntity.of(Optional.of(oAuth2Service.getRequestID(null)));
    }

    @PostMapping("/oauth2/request/polling")
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

    @PostMapping("/oauth2/request/get-session")
    public ResponseEntity<?> getSession(@RequestBody RequestDTO request) {
        try {
            return ResponseEntity.ok(oAuth2Service.getSession(request));
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
