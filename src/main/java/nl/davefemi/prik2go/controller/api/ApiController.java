package nl.davefemi.prik2go.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.controller.exceptions.Prik2GoException;
import nl.davefemi.prik2go.controller.exceptions.BranchException;
import nl.davefemi.prik2go.service.domain.DomainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Tag(name = "locations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/locations")
public class ApiController {
    private static final Logger logger = Logger.getLogger(ApiController.class.getName());
    private final DomainService domainService;

    @Operation(operationId = "get-branches")
    @GetMapping("/get-branches")
    public ResponseEntity<?> getBranches(){
        logger.info("Branches successfully retrieved");
        return ResponseEntity.ok(domainService.getLocations());
    }

    @Operation(operationId = "get-branch-customers")
    @GetMapping("/get-customers")
    public ResponseEntity<?> getCustomers(@RequestParam("location") String location) throws Prik2GoException {
        logger.info("Custumers for [" + location + "] successfully retrieved");
        return ResponseEntity.ok(domainService.getBranchDTO(location));
    }

    @Operation(operationId = "get-branch-status")
    @GetMapping("/get-status")
    public ResponseEntity<?> getBranchStatus(@RequestParam("location") String location) throws BranchException {
        logger.info("Status for [" + location + "] successfully retrieved");
        return ResponseEntity.ok(domainService.getBranchStatus(location));
    }

    @Operation(operationId = "change-branch-status")
    @PutMapping("/change-status")
    public ResponseEntity<?> changeBranchStatus(@RequestParam("location") String location) throws BranchException {
        domainService.changeBranchStatus(location);
        logger.info("Status for [" + location + "] set to " + domainService.getBranchStatus(location));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
