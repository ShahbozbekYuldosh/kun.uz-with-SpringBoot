package dasturlash.uz.service;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.entity.ProfileRoleEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.repository.ProfileRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}

