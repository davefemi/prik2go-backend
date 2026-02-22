package nl.davefemi.prik2go.service.auth.oauth2client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import nl.davefemi.prik2go.authorization.EnvHelper;
import nl.davefemi.prik2go.data.entity.OAuthClientEntity;
import nl.davefemi.prik2go.data.repository.OAuthClientRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AzureClient implements OAuth2Client{
    private final EnvHelper envHelper;
    private final String PROVIDER = "azure";
    private final OAuthClientRepository repository;
    private OAuthClientEntity entity;
    @Value("${external.uri.azure.auth-uri}")
    private String endpoint;
    @Value("${external.uri.azure.redirect-uri}")
    private String redirect_uri;

    @PostConstruct
    private void init(){
        entity = repository.getOauthClientEntityByName(PROVIDER);
    }
    public String getProviderName(){
        return PROVIDER;
    }

    @Override
    public OAuthClientEntity getOAuthClientEntity() {
        return entity;
    }

    @Override
    public String getClientURL() {
        return String.format(envHelper.getBaseUrl()+envHelper.getOauthLogin(), PROVIDER);
    }

    @Override
    public String getAuthorizationEndpoint() {
        return "azure";
    }


}
