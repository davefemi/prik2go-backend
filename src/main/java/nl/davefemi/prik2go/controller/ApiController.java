package nl.davefemi.prik2go.controller;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.UserAccountDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.AuthorizationException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import nl.davefemi.prik2go.service.AuthService;
import nl.davefemi.prik2go.service.AuthServiceInterface;
import nl.davefemi.prik2go.service.DomainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private/locations")
public class ApiController {
    private static final Logger logger = Logger.getLogger(ApiController.class.getName());
    private final DomainService domainService;
    private final AuthServiceInterface authService;

    @PostMapping("/get-branches")
    public ResponseEntity<?> getBranches(@RequestBody String userId){
//        authService.validateSession(userId);
        try {
            logger.info("Branches successfully retrieved");
            return ResponseEntity.of(Optional.of(domainService.getVestigingLocaties()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/get-customers")
    public ResponseEntity<?> getCustomer(@RequestParam("location") String location, @RequestBody String userId){
        authService.validateSession(userId);
        try {
            logger.info("Custumers for [" + location + "] successfully retrieved");
            return ResponseEntity.of(Optional.of(domainService.getKlantenDTO(location)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/get-status")
    public ResponseEntity<?> getBranchStatus(@RequestParam("location") String location, @RequestBody String userId){
        authService.validateSession(userId);
        try{
            logger.info("Status for [" + location + "] successfully retrieved");
            return ResponseEntity.of(Optional.of(domainService.getVestigingStatus(location)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());        }
    }

    @PutMapping("/change-status")
    public ResponseEntity<?> changeBranchStatus(@RequestParam("location") String location, @RequestBody String userId) {
        authService.validateSession(userId);
        try {
            domainService.veranderVestigingStatus(location);
            logger.info("Status for [" + location + "] set to " + domainService.getVestigingStatus(location));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (VestigingException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
