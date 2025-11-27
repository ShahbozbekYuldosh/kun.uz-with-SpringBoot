package dasturlash.uz.service;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.entity.ProfileRoleEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.repository.ProfileRoleRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileRoleService {

    private final ProfileRoleRepository profileRoleRepository;

    public void create(ProfileEntity profile, ProfileRole role) {
        ProfileRoleEntity entity = new ProfileRoleEntity();
        entity.setProfile(profile);
        entity.setRole(role);
        entity.setCreatedDate(LocalDateTime.now());
        profileRoleRepository.save(entity);
    }

    void deleteRoles(Integer profileId) {
        profileRoleRepository.deleteByProfileId(profileId);
    }

    @NotEmpty(message = "Role bo‘sh bo‘lmasligi kerak") List<ProfileRole> getByProfileId(Integer profileId) {
        return null;
    }

}

