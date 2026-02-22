package nl.davefemi.prik2go.service.auth.oauth2client;

import nl.davefemi.prik2go.data.entity.OAuthClientEntity;

public interface OAuth2Client {

    String getProviderName();
    OAuthClientEntity getOAuthClientEntity();
    String getClientURL();
    String getAuthorizationEndpoint();
}
