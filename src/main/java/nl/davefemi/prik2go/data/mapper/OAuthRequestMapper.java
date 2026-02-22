package nl.davefemi.prik2go.data.mapper;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.OAuthResponseDTO;
import nl.davefemi.prik2go.data.entity.OAuthClientEntity;
import nl.davefemi.prik2go.data.entity.OAuthRequestEntity;
import nl.davefemi.prik2go.data.repository.OAuthClientRepository;
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
