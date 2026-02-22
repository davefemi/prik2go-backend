package nl.davefemi.prik2go.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.RequestDTO;
import nl.davefemi.prik2go.exceptions.AuthorizationException;
import nl.davefemi.prik2go.service.auth.OAuth2Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    /**
     * After obtaining a valid requestId, this method can be for authorization at Google's auth server.
     * @param requestId
     * @param userId
     * @param req
     * @return
     */
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

    /**
     * In order to start a login flow to link an existing and logged-in user to a Google account,
     * a Request entity must be obtained which is valid for a set period of time and can be referred to
     * log in an authenticated Google user.
     * @param req
     * @param principal containing the userId of the user that want to link a Google account
     * @return ResponsEntity containing a OAuthResponseDTO containing: uuid requestCode, secret pollingInterval,
     * expiration of request and the url leading for authorization.
     */
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

    /**
     * In order to start a login flow via a Google account, a Request entity must be obtained which is
     * valid for a set period of time and can be referred to log in an authenticated Google user
     * @return ResponsEntity containing a OAuthResponseDTO containing: uuid requestCode, secret pollingInterval,
     * expiration of request and the url leading for authorization.
     */
    @GetMapping("/oauth2/request/start")
    public ResponseEntity<?> getRequest(){
        return ResponseEntity.of(Optional.of(oAuth2Service.getRequestID(null)));
    }

    /**
     * Polling method to verify if a user has been authenticated by Google's authorization server.
     * You will need to have obtained a valid Request entity to be able to submit a valid polling request.
     * @param request RequestDTO containing the request code and request secret
     * @return true for an authenticated user and false for an unauthenticated user
     */
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
