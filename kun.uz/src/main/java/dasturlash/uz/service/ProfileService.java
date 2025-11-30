package dasturlash.uz.service;

import dasturlash.uz.dto.profile.ProfileCreateDTO;
import dasturlash.uz.dto.profile.ProfileDTO;
import dasturlash.uz.dto.profile.ProfileDetailUpdateDTO;
import dasturlash.uz.dto.profile.ProfileUpdateByAdminDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileRoleService profileRoleService;

    public ProfileDTO create(@Valid ProfileCreateDTO dto, Integer adminId) {

        if (dto.getRoleList().contains(ProfileRole.ROLE_ADMIN)) {
            throw new AppBadException("Admin boshqa Admin yarata olmaydi.");
        }

        // 2. Username bandligini tekshirish
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            throw new AppBadException("Username (" + dto.getUsername() + ") allaqachon band.");
        }

        // 3. Entity yaratish
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setUsername(dto.getUsername());

        // Parolni hash qilish
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity.setStatus(dto.getProfileStatus());
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());

        profileRepository.save(entity);

        // 4. Role'larni kiritish
        dto.getRoleList().forEach(role -> profileRoleService.create(entity, role));

        return toInfoDTO(entity);
    }

    // ADMIN: 2. Get By Id - Metod nomi getById
    public ProfileDTO getById(Integer id) {
        ProfileEntity entity = get(id);
        return toInfoDTO(entity);
    }

    // ADMIN: 3. Update Profile (Admin boshqa profilni o'zgartiradi)
    public ProfileDTO updateByAdmin(Integer targetProfileId, ProfileUpdateByAdminDTO dto) {
        ProfileEntity entity = get(targetProfileId);

        // Name, Surname, Username, Status, Role o'zgarishi mumkin
        entity.setName(dto.getName());

        // Username tekshiruvi: Agar username o'zgargan bo'lsa va yangi username band bo'lsa
        if (!entity.getUsername().equals(dto.getUsername())) {
            Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
            if (optional.isPresent()) {
                throw new AppBadException("Username (" + dto.getUsername() + ") allaqachon band.");
            }
            entity.setUsername(dto.getUsername());
        }

        entity.setStatus(dto.getProfileStatus());

        profileRepository.save(entity);

        profileRoleService.deleteRoles(targetProfileId);
        dto.getRoleList().forEach(role -> profileRoleService.create(entity, role));

        return toInfoDTO(entity);
    }

    // ANY: 4. Update Profile Detail (Foydalanuvchi o'zini o'zgartiradi)
    public ProfileDTO updateDetail(Integer currentUserId, ProfileDetailUpdateDTO dto) {
        ProfileEntity entity = get(currentUserId);

        entity.setName(dto.getName());

        profileRepository.save(entity);

        return toInfoDTO(entity);
    }

    // ADMIN: 5. Profile List (Pagination)
    public Page<ProfileDTO> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<ProfileEntity> entityPage = profileRepository.findAll(pageable);

        List<ProfileDTO> dtoList = entityPage.getContent().stream()
                .map(this::toInfoDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    // ADMIN: 6. Delete Profile By Id (Logic delete)
    public void delete(Integer id) {
        ProfileEntity entity = get(id);

        // Agar o'chirilayotgan profil ADMIN bo'lsa, xato berish kerak
        if (profileRoleService.getRole(id).orElse(null) == ProfileRole.ROLE_ADMIN) {
            throw new AppBadException("Admin profilini o'chirishga ruxsat yo'q.");
        }

        profileRepository.updateVisible(false, id);
//         AttachService.delete(entity.getPhotoId()); // Rasmni ham o'chirish kerak
    }

    // ANY: 7. Update Photo (Image)
    public void updatePhoto(Integer currentUserId, String attachId) {
        ProfileEntity entity = get(currentUserId);
        String oldAttachId = entity.getPhotoId();

        profileRepository.updatePhotoId(attachId, currentUserId);

    }

    // ANY: 8. Update Password
    public void updatePassword(Integer currentUserId, String oldPassword, String newPassword) {
        ProfileEntity entity = get(currentUserId);

        // Eski parolni tekshirish
        if (!passwordEncoder.matches(oldPassword, entity.getPassword())) {
            throw new AppBadException("Joriy parol noto'g'ri.");
        }

        // Yangi parolni hashlab saqlash
        profileRepository.updatePassword(passwordEncoder.encode(newPassword), currentUserId);
    }

    // ADMIN: 9. Filter (Specification or Criteria API or custom query)
    public Page<ProfileDTO> filter(String query, ProfileRole role, LocalDateTime createdFrom, LocalDateTime createdTo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getList(page, size);
    }


    // Yordamchi metodlar

    public ProfileEntity get(Integer id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Profile not found: " + id));
    }

    private ProfileDTO toInfoDTO(ProfileEntity entity) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUsername(entity.getUsername());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setStatus(entity.getStatus());
        dto.setPhotoId(entity.getPhotoId()); // Photo ID

        // Role listni olish (ProfileRoleService orqali)
        dto.setRoleList(profileRoleService.getProfileRoles(entity.getId()));

        return dto;
    }
}