package nl.davefemi.prik2go.controller;

import io.jsonwebtoken.JwtException;
import nl.davefemi.prik2go.exceptions.ErrorBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorBody body = new ErrorBody();
        body.setTitle(request.getSessionId());
        body.setMessage(ex.getMessage());
        body.setStatus(HttpStatus.BAD_REQUEST.value());
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(
                ex,
                body,
                headers,
                HttpStatus.BAD_REQUEST,
                request
        );
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwtExceptions(JwtException ex) {
        ErrorBody body = new ErrorBody();
        body.setTitle("Authorization");
        body.setMessage(ex.getMessage());
        body.setStatus(HttpStatus.UNAUTHORIZED.value());
        HttpHeaders headers = new HttpHeaders();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

}


