package nl.davefemi.prik2go.service.auth.oauth2client;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.authorization.EnvHelper;
import nl.davefemi.prik2go.data.entity.auth.OAuthClientEntity;

@RequiredArgsConstructor
public class OAuth2Client {
    private final OAuthClientEntity entity;
    private final EnvHelper envHelper;

    public String getProviderName(){
        return entity.getName().toLowerCase();
    }

    public OAuthClientEntity getOAuthClientEntity() {
        return entity;
    }

    public String getClientURL() {
        return String.format(envHelper.getBaseUrl()+envHelper.getOauthLogin(), getProviderName());
    }
}