package nl.davefemi.prik2go.service.auth.oauth2client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.authorization.EnvHelper;
import nl.davefemi.prik2go.data.entity.auth.OAuthClientEntity;
import nl.davefemi.prik2go.data.repository.auth.OAuthClientRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutlookClient implements OAuth2Client{
    private final EnvHelper envHelper;
    private final String PROVIDER = "outlook";
    private final OAuthClientRepository repository;
    private OAuthClientEntity entity;

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

}
