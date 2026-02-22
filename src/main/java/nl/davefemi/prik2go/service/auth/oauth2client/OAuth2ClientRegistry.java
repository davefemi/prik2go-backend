package nl.davefemi.prik2go.service.auth.oauth2client;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2ClientRegistry {
    private final OutlookClient AZURE;
    private final GoogleClient GOOGLE;
    private final List<OAuth2Client> clients = new ArrayList<>();
    private Map<String, OAuth2Client> providers = new HashMap<>();

    @PostConstruct
    private void init(){
        clients.add(AZURE);
        clients.add(GOOGLE);
        for (OAuth2Client c : clients){
            providers.put(c.getProviderName().toLowerCase(), c);
        }
    }

    public OAuth2Client getOAuth2Client(String provider) throws ApplicatieException {
        if (!providers.containsKey(provider.toLowerCase()))
            throw new ApplicatieException("Provider does not exist");
        return providers.get(provider.toLowerCase());
    }
}
