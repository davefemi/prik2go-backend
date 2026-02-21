package nl.davefemi.prik2go.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class MSAuthController {

    @GetMapping("")
    public ResponseEntity<?> loginUser() {
//        try {
//            if (!oAuth2Service.validateRequest(requestId))
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Request denied");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
//        HttpSession sess = req.getSession(true);
//        sess.setAttribute("request", requestId);
//        sess.setAttribute("userId", userId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/authorization/azure"))
                .build();
    }


}
