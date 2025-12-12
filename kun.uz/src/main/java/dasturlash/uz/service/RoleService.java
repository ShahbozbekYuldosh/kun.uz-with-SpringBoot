package dasturlash.uz.service;

import dasturlash.uz.dto.RoleChangeDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.entity.ProfileRoleEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.repository.ProfileRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final ProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;

    @Transactional
    public void changeUserRole(RoleChangeDTO dto) {

        //------------------- 1. Profilni olish --------------------------
        ProfileEntity profile = profileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new AppBadException("Profile topilmadi"));

        //---------------- 2. Eski rollarni o'chirish --------------------
        if (profile.getProfileRoles() != null) {
            profileRoleRepository.deleteAll(profile.getProfileRoles());
            profile.getProfileRoles().clear();
        }

        //-------------- 3. Yangi rol yaratish va saqlash ------------------
        ProfileRoleEntity newRole = new ProfileRoleEntity();
        newRole.setProfile(profile);
        newRole.setRole(ProfileRole.valueOf(dto.getNewRole()));
        newRole.setCreatedDate(LocalDateTime.now());

        profileRoleRepository.save(newRole);
        profile.getProfileRoles().add(newRole);
    }
}
