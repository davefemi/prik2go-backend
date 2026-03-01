package nl.davefemi.prik2go.controller.api;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.exceptions.Prik2GoException;
import nl.davefemi.prik2go.exceptions.BranchException;
import nl.davefemi.prik2go.service.domain.DomainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private/locations")
public class ApiController {
    private static final Logger logger = Logger.getLogger(ApiController.class.getName());
    private final DomainService domainService;

    @PostMapping("/get-branches")
    public ResponseEntity<?> getBranches(@RequestBody String userId){
        logger.info("Branches successfully retrieved");
        return ResponseEntity.ok(domainService.getLocations());
    }

    @PostMapping("/get-customers")
    public ResponseEntity<?> getCustomer(@RequestParam("location") String location, @RequestBody String userId) throws Prik2GoException {
        logger.info("Custumers for [" + location + "] successfully retrieved");
        return ResponseEntity.ok(domainService.getBranchDTO(location));
    }

    @PostMapping("/get-status")
    public ResponseEntity<?> getBranchStatus(@RequestParam("location") String location, @RequestBody String userId) throws BranchException {
        logger.info("Status for [" + location + "] successfully retrieved");
        return ResponseEntity.ok(domainService.getBranchStatus(location));
    }

    @PutMapping("/change-status")
    public ResponseEntity<?> changeBranchStatus(@RequestParam("location") String location, @RequestBody String userId) throws BranchException {
        domainService.changeBranchStatus(location);
        logger.info("Status for [" + location + "] set to " + domainService.getBranchStatus(location));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
