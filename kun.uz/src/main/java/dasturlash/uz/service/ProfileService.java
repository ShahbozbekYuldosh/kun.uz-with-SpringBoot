package dasturlash.uz.service;

import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.dto.RegistrationDTO; // RegistrationDTO ni import qilamiz
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*; // Pagination uchun kerakli importlar
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List; // List import
import java.util.Optional;
import java.util.stream.Collectors; // Collectors import

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileRoleService profileRoleService;

    // getById(Integer profileId) metodini o'zgartiramiz, chunki get(id) yordamchi metodi bor
    // public ProfileEntity getById(Integer profileId){ // Bu metod nomi xato edi, get(id) bilan bir xil
    //     return profileRepository.findByIdAndVisibleTrue(profileId).orElseThrow( () -> {
    //         throw new AppBadException("Profile Not Found");
    //     });
    // }

    // 1. Create Profile (ADMIN) - RegistrationDTO o'rniga ProfileCreateDTO ishlatilishi kerak.
    // Dastlabki talab ProfileCreateDTO ni ko'rsatgan edi.
    public ProfileDTO create(@Valid RegistrationDTO dto, Integer adminId) {

        // 1. Rollarni tekshirish: ADMIN faqat MODERATOR yoki PUBLISHER yaratishi mumkin
        if (dto.getRoleList().contains(ProfileRole.ROLE_ADMIN)) { // dto.getRoleList() bo'lishi kerak
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

        entity.setStatus(dto.getStatus()); // getProfileStatus o'rniga getStatus
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());

        profileRepository.save(entity);

        // 4. Role'larni kiritish
        dto.getRoleList().forEach(role -> profileRoleService.create(entity, role)); // getProfileRole() o'rniga getRoleList()

        return toInfoDTO(entity);
    }

    // ADMIN: 2. Get By Id - Metod nomi getById
    public ProfileDTO getById(Integer id) {
        ProfileEntity entity = get(id); // yordamchi metod orqali topish
        return toInfoDTO(entity);
    }

    // ADMIN: 3. Update Profile (Admin boshqa profilni o'zgartiradi)
    public ProfileDTO updateByAdmin(Integer targetProfileId, RegistrationDTO dto) {
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

        // Rollarni yangilash (Eski rollarni o'chirib, yangilarni kiritish)
        profileRoleService.deleteRoles(targetProfileId);
        dto.getRoleList().forEach(role -> profileRoleService.create(entity, role));

        return toInfoDTO(entity);
    }

    // ANY: 4. Update Profile Detail (Foydalanuvchi o'zini o'zgartiradi)
    public ProfileDTO updateDetail(Integer currentUserId, RegistrationDTO dto) {
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

        // Logik o'chirish (visible = false)
        profileRepository.updateVisible(id, false);
        // AttachService.delete(entity.getPhotoId()); // Rasmni ham o'chirish kerak
    }

    // ANY: 7. Update Photo (Image)
    public void updatePhoto(Integer currentUserId, String attachId) {
        ProfileEntity entity = get(currentUserId);
        String oldAttachId = entity.getPhotoId();

        // Yangi attachId ni o'rnatish
        profileRepository.updatePhotoId(currentUserId, attachId);

    }

    // ANY: 8. Update Password
    public void updatePassword(Integer currentUserId, String oldPassword, String newPassword) {
        ProfileEntity entity = get(currentUserId);

        // Eski parolni tekshirish
        if (!passwordEncoder.matches(oldPassword, entity.getPassword())) {
            throw new AppBadException("Joriy parol noto'g'ri.");
        }

        // Yangi parolni hashlab saqlash
        profileRepository.updatePassword(currentUserId, passwordEncoder.encode(newPassword));
    }

    // ADMIN: 9. Filter (Specification or Criteria API or custom query)
    public Page<ProfileDTO> filter(String query, ProfileRole role, LocalDateTime createdFrom, LocalDateTime createdTo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getList(page, size); // Filter mantiqi bo'lmasa, umumiy list qaytariladi
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