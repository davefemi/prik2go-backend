package nl.davefemi.prik2go.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import java.time.Instant;

@Data
@JsonSerialize
public class ErrorBody {
    private String timestamp = Instant.now().toString();
    private String title;
    private int status;
    private String message;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
