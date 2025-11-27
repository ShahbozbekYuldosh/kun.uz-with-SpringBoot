package dasturlash.uz.service;

import dasturlash.uz.dto.AuthorizationDTO;
import dasturlash.uz.dto.MessageDTO;
import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final ProfileRoleService profileRoleService;
    private final EmailSendingService emailSendingService;


    private String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public String register(RegistrationDTO dto) {

        // 1. Username bandligiga tekshirish
        Optional<ProfileEntity> optional =
                profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());

        if (optional.isPresent()) {
            ProfileEntity oldProfile = optional.get();

            // Agar eski profil aktiv emas va tasdiqlash jarayonida bo'lsa
            if (oldProfile.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
                profileRoleService.deleteRoles(oldProfile.getId());
                profileRepository.delete(oldProfile);
                // eski ro'yxatdan o‘tish jarayonini tozalaymiz
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

        // 4. Tasdiqlash kodini yuborish kerak
        String otp = generateOtp();  // OTP yaratish funksiyasi
        String verifyUrl = "https://your-app.com/verify?otp=" + otp;

        MessageDTO emaildto = new MessageDTO();
        emaildto.setToAccount(dto.getUsername());
        emaildto.setSubject("Email Verification");
        emaildto.setName(dto.getName());
        emaildto.setOtp(otp);
        emaildto.setVerifyUrl(verifyUrl);

        emailSendingService.sendMimeMessage(emaildto);

        return "Registration successful. Please verify your account.";

    }

    public ProfileDTO login(AuthorizationDTO dto) {
        Optional<ProfileEntity> profileOptional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (profileOptional.isEmpty()) {
            throw new AppBadException("Username or password wrong");
        }
        ProfileEntity entity = profileOptional.get();
        if (!passwordEncoder.matches(dto.getPassword(), entity.getPassword())) {
            throw new AppBadException("Username or password wrong");
        }
        if (!entity.getStatus().equals(ProfileStatus.ACTIVE)) {
            throw new AppBadException("User in wrong status");
        }
        // status
        ProfileDTO response = new ProfileDTO();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setUsername(entity.getUsername());
        response.setRoleList(profileRoleService.getByProfileId(entity.getId()));
        return response;
    }


}

