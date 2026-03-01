package nl.davefemi.prik2go.data.mapper.auth;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.auth.OAuthResponseDTO;
import nl.davefemi.prik2go.data.entity.auth.OAuthClientEntity;
import nl.davefemi.prik2go.data.entity.auth.OAuthRequestEntity;
import nl.davefemi.prik2go.data.repository.auth.OAuthClientRepository;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthRequestMapper {
    final OAuthClientRepository oAuthClientRepository;

    public OAuthRequestEntity mapToEntity(OAuthResponseDTO dto, String hashSecret, boolean authorized, OAuthClientEntity provider){
        OAuthRequestEntity entity = new OAuthRequestEntity();
        entity.setRequestId(dto.getRequestCode());
        entity.setProvider(provider);
        if (entity.getProvider()==null)
            throw new OpenApiResourceNotFoundException("Provider does not exist");
        entity.setSecret(hashSecret);
        entity.setExpiresAt(dto.getExpiresAt());
        entity.setAuthorized(authorized);
        return entity;
    }
}
