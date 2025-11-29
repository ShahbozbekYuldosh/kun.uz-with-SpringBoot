package dasturlash.uz.service;

import dasturlash.uz.dto.*;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final ProfileRoleService profileRoleService;
    private final EmailSendingService emailSendingService;
    private final ProfileService profileService;
    private final JwtUtil jwtUtil;
    @Value("${app.frontend.url:https://kun.uz}")
    private String frontendUrl;

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

        // JWT token yaratish (15 daqiqa amal qiladi)
        String verificationToken = jwtUtil.generateEmailVerificationToken(
                entity.getId(),
                entity.getUsername()
        );
        String verifyUrl = frontendUrl + "/auth/verify?token=" + verificationToken;

        MessageDTO emaildto = new MessageDTO();
        emaildto.setToAccount(dto.getUsername());
        emaildto.setSubject("Email Verification");
        emaildto.setName(dto.getName());
        emaildto.setVerifyUrl(verifyUrl);

        emailSendingService.sendMimeMessage(emaildto);

        return "Registration successful. Please verify your account.";

    }

    @Transactional
    public String verifyEmail(String token) {
        try {
            // Token ni parse qilish va validate qilish
            Claims claims = jwtUtil.extractClaims(token);

            // Token type tekshirish
            String tokenType = claims.get("type", String.class);
            if (!"EMAIL_VERIFICATION".equals(tokenType)) {
                throw new AppBadException("Invalid token type");
            }

            // Profile ID olish
            Integer profileId = claims.get("profileId", Integer.class);
            String username = claims.getSubject();

            // Profile topish
            ProfileEntity profileEntity = profileService.getById(profileId);

            // Username tekshirish (qo'shimcha xavfsizlik)
            if (!profileEntity.getUsername().equals(username)) {
                throw new AppBadException("Invalid token");
            }

            // Status tekshirish
            if (profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
                return "Email already verified";
            }

            if (!profileEntity.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
                throw new AppBadException("Invalid profile status");
            }

            // Verifikatsiya muvaffaqiyatli
            profileEntity.setStatus(ProfileStatus.ACTIVE);
            profileRepository.save(profileEntity);

            return "Email verified successfully. You can now login.";

        } catch (JwtException e) {
            throw new AppBadException("Invalid or expired verification token");
        } catch (Exception e) {
            throw new AppBadException("Verification failed: " + e.getMessage());
        }
    }

    @Transactional
    public String resendVerificationEmail(String username) {
        // Profile topish
        ProfileEntity profileEntity = profileRepository.findByUsernameAndVisibleTrue(username)
                .orElseThrow(() -> new AppBadException("User not found"));

        // Status tekshirish
        if (profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
            throw new AppBadException("User is already verified");
        }

        if (!profileEntity.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
            throw new AppBadException("Invalid profile status");
        }

        // Yangi JWT token yaratish
        String verificationToken = jwtUtil.generateEmailVerificationToken(
                profileEntity.getId(),
                profileEntity.getUsername()
        );

        String verifyUrl = frontendUrl + "/auth/verify?token=" + verificationToken;

        // Email yuborish
        MessageDTO dto = new MessageDTO();
        dto.setToAccount(username);
        dto.setSubject("Email Verification - New Link");
        dto.setName(profileEntity.getName());
        dto.setVerifyUrl(verifyUrl);

        emailSendingService.sendMimeMessage(dto);

        return "New verification link sent to your email";
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {
        // 1. Username bo'yicha profilni topish
        ProfileEntity profileEntity = profileRepository.findByUsernameAndVisibleTrue(loginDTO.getUsername())
                .orElseThrow(() -> new AppBadException("Login yoki parol noto'g'ri"));

        // 2. Parolni tekshirish (BCrypt yordamida)
        boolean matches = passwordEncoder.matches(loginDTO.getPassword(), profileEntity.getPassword());
        if (!matches) {
            throw new AppBadException("Login yoki parol noto'g'ri");
        }

        // 3. Profil Statusini tekshirish
        if (!profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
            // Agar status REGISTRATION_PROGRESS bo'lsa, emailni tasdiqlash kerak
            if (profileEntity.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
                throw new AppBadException("Email tasdiqlanmagan. Iltimos, emailingizni tekshiring.");
            }
            // Boshqa har qanday nofaol status (masalan, BLOCKED, DELETED)
            throw new AppBadException("Sizning profilingiz faol emas.");
        }

        // 4. Role'larni olish (Agar ProfileRoleService bu metodni taqdim etsa)
        // Login tokeniga role'lar kiritilishi muhim
        ProfileRole role = (ProfileRole) profileRoleService.getRole(profileEntity.getId())
                .orElseThrow(() -> new AppBadException("Profil role'lari topilmadi"));


        // 5. JWT Token yaratish (Auth Token, odatda 24 soat amal qiladi)
        String authToken = jwtUtil.generateAccessToken(profileEntity.getId(), profileEntity.getUsername(), role);

        // 6. Response DTO yaratish
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setName(profileEntity.getName());
        responseDTO.setUsername(profileEntity.getUsername());
        responseDTO.setRole(role.name()); // Role enumdan Stringga o'tkazish
        responseDTO.setToken(authToken);

        return responseDTO;
    }


}

