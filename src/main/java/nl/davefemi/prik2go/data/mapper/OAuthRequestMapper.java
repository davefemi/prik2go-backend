package nl.davefemi.prik2go.data.mapper;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.OAuthResponseDTO;
import nl.davefemi.prik2go.data.entity.OAuthRequestEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthRequestMapper {

    public OAuthRequestEntity mapToEntity(OAuthResponseDTO dto, String hashSecret, boolean authorized){
        OAuthRequestEntity entity = new OAuthRequestEntity();
        entity.setRequestId(dto.getRequestCode());
        entity.setSecret(hashSecret);
        entity.setExpiresAt(dto.getExpiresAt());
        entity.setAuthorized(authorized);
        return entity;
    }
}
