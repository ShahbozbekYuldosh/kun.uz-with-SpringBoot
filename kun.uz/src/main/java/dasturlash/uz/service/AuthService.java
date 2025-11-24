package dasturlash.uz.service;

import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final ProfileRoleService profileRoleService;
    private final SmsEmailService smsEmailService;

    public String register(RegistrationDTO dto) {

        // 1. Username bandligiga tekshirish
        Optional<ProfileEntity> optional =
                profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());

        if (optional.isPresent()) {
            ProfileEntity oldProfile = optional.get();

            // Agar eski profil aktiv emas va tasdiqlash jarayonida bo'lsa
            if (oldProfile.getStatus().equals(ProfileStatus.REGISTRETION_PROGRESS)) {
                profileRepository.delete(oldProfile); // eski ro'yxatdan o‘tish jarayonini tozalaymiz
            } else {
                throw new AppBadException("User already exists");
            }
        }

        // 2. Yangi profil yaratish
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setUsername(dto.getUsername());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setStatus(ProfileStatus.REGISTRATION_PROGRESS);
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());

        profileRepository.save(entity);

        // 3. Default Role qo‘shish
        profileRoleService.create(entity, ProfileRole.ROLE_USER);

        // 4. Tasdiqlash kodini yuborish
        smsEmailService.sendVerificationCode(entity);

        return "Registration successful. Please verify your account.";
    }
}

