package nl.davefemi.prik2go.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class ErrorController {

    @GetMapping("/error")
    public ResponseEntity redirectToError(){
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/error.html"))
                .build();
    }

}
