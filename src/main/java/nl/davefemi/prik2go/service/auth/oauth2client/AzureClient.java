package nl.davefemi.prik2go.service.auth.oauth2client;

import org.springframework.stereotype.Component;

@Component
public class AzureClient implements OAuth2Client{
    final String ISSUER_ID = "AZURE";

    public String getIssuerId(){
        return ISSUER_ID;
    }
}
