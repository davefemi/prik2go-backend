package nl.davefemi.prik2go.service.auth.oauth2client;

import org.springframework.stereotype.Component;

@Component
public class GoogleClient implements OAuth2Client {
    final String ISSUER_ID = "GOOGLE";

    @Override
    public String getIssuerId(){
        return ISSUER_ID;
    }
}
