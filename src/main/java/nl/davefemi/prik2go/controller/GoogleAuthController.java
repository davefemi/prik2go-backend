package nl.davefemi.prik2go.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.service.OAuth2Service;
import org.springframework.boot.actuate.info.ProcessInfoContributor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("oauth2/")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final OAuth2Service oauth2Service;

    @GetMapping("login/google")
    public ResponseEntity<Void> loginUser(@RequestParam("state") String deviceCode, HttpServletRequest req){
        req.getSession(true).setAttribute("device", deviceCode);
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
//        oauth2Service.validateUser(email);
        return ResponseEntity.status(HttpStatus.OK).body("Go back to the app my friend!" + result);
    }

    @PostMapping("device/start")
    public ResponseEntity<?> registerDevice(){
        return ResponseEntity.of(Optional.of(oauth2Service.getDevicePolling()));
    }
}
