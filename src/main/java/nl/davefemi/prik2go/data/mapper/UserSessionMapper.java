package nl.davefemi.prik2go.data.mapper;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.UserSessionDTO;
import nl.davefemi.prik2go.data.entity.UserAccountEntity;
import nl.davefemi.prik2go.data.entity.UserSessionEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionMapper {
    private final UserAccountMapper userAccountMapper;

    public UserSessionEntity mapToEntity(UserSessionDTO dto, UserAccountEntity user){
        UserSessionEntity entity = new UserSessionEntity();
        entity.setUserId(user);
        entity.setToken(dto.getToken());
        entity.setIssuedAt(dto.getIssuedAt());
        entity.setExpiresAt(dto.getExpiresAt());
        entity.setTokenId(dto.getTokenId());
        return entity;
    }
}
