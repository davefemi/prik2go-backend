package nl.davefemi.prik2go.authorization;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Component
@Getter
public class EnvHelper {
    @Value("${DOMAIN_BASE_URL}")
    private String baseUrl;

    @PostConstruct
    private void init(){
        System.out.println(baseUrl);
    }
}
