package nl.davefemi.prik2go.service.auth.oauth2client;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.davefemi.prik2go.authorization.EnvHelper;
import nl.davefemi.prik2go.data.entity.auth.OAuthClientEntity;
import nl.davefemi.prik2go.data.repository.auth.OAuthClientRepository;
import nl.davefemi.prik2go.controller.exceptions.Prik2GoException;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2ClientRegistry {
    private final EnvHelper envHelper;
    private final OAuthClientRepository oAuthClientRepository;
    private final Map<String, OAuth2Client> providers = new HashMap<>();

    @PostConstruct
    private void init(){
        for (OAuthClientEntity e : oAuthClientRepository.findAll()){
            OAuth2Client client = new OAuth2Client(e, envHelper);
            providers.put(client.getProviderName().toLowerCase(), client);
            log.info("Added client {}", client.getProviderName());
        }
    }

    public OAuth2Client getOAuth2Client(String provider) throws Prik2GoException {
        if (!providers.containsKey(provider.toLowerCase()))
            throw new Prik2GoException("Provider does not exist");
        return providers.get(provider.toLowerCase());
    }
}