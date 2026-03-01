package nl.davefemi.prik2go.data.mapper.identity;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.identity.UserAccountDTO;
import nl.davefemi.prik2go.data.entity.identity.UserAccountEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAccountMapper {

    public UserAccountEntity mapToEntity(UserAccountDTO dto) {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setRole(dto.getRole());
        entity.setPassword(dto.getPassword());
        return entity;
    }

    public UserAccountDTO mapToDTO(UserAccountEntity entity) {
        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(entity.getId());
        dto.setUser(entity.getUserid());
        dto.setEmail(entity.getEmail());
        dto.setName(entity.getName());
        dto.setRole(entity.getRole());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
