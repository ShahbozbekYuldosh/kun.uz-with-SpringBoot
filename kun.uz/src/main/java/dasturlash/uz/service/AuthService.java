package dasturlash.uz.service;

import dasturlash.uz.dto.*;
import dasturlash.uz.dto.sms.SmsVerificationDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileContactType;
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
    private final SmsSendingService smsSendingService;
    private final ProfileService profileService;
    private final JwtUtil jwtUtil;
    @Value("${app.frontend.url:}")
    private String frontendUrl;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_REGEX = "^\\+?[0-9]{9,15}$";

    private String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
    private ProfileContactType getContactType(String username) {
        if (username.matches(EMAIL_REGEX)) {
            return ProfileContactType.EMAIL;
        }
        if (username.matches(PHONE_REGEX)) {
            return ProfileContactType.PHONE;
        }
        throw new AppBadException("Username not valid: Must be a valid email or phone number.");
    }


    @Transactional
    public String register(RegistrationDTO dto) {
        ProfileContactType contactType = getContactType(dto.getUsername());
        ProfileEntity entity; // ✨ Tashqariga chiqarildi

        Optional<ProfileEntity> optional =
                profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());

        if (optional.isPresent()) {
            ProfileEntity oldProfile = optional.get();

            // Agar tasdiqlash jarayonida bo'lsa, uni O'CHIRISH O'RNIGA YANGILAYMIZ
            if (oldProfile.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {

                // Eski role'larni o'chiramiz va yangisini yaratamiz, chunki profil ID bir xil
                profileRoleService.deleteRoles(oldProfile.getId());

                // Mavjud entityni yangi ma'lumotlar bilan to'ldiramiz
                entity = oldProfile;
                entity.setName(dto.getName());
                entity.setPassword(passwordEncoder.encode(dto.getPassword()));
                entity.setCreatedDate(LocalDateTime.now());
                // Status, username, contactType allaqachon bor va o'zgarmaydi.

            } else {
                // Agar ACTIVE yoki BLOCKED bo'lsa, ruxsat yo'q
                throw new AppBadException("User already exists");
            }
        } else {
            // Yangi profil yaratish (avvalgi mantiq)
            entity = new ProfileEntity();
            entity.setName(dto.getName());
            entity.setUsername(dto.getUsername());
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
            entity.setContactType(contactType);
            entity.setVisible(true);
        }
        entity.setStatus(ProfileStatus.REGISTRATION_PROGRESS);


        String resultMessage;

        if (contactType.equals(ProfileContactType.EMAIL)) {

            profileRepository.save(entity);
            profileRoleService.create(entity, ProfileRole.ROLE_USER);

            String verificationToken = jwtUtil.generateEmailVerificationToken(
                    entity.getId(),
                    dto.getUsername()
            );
            String verifyUrl = frontendUrl + "/auth/verify/email?token=" + verificationToken;

            MessageDTO emaildto = new MessageDTO();
            emaildto.setToAccount(dto.getUsername());
            emaildto.setSubject("Email Verification");
            emaildto.setName(dto.getName());
            emaildto.setVerifyUrl(verifyUrl);

            emailSendingService.sendMimeMessage(emaildto);
            resultMessage = "Registration successful. Please verify your account via email.";

        } else {
            String otp = generateOtp();

            entity.setVerificationCode(otp);
            entity.setVerificationCodeGeneratedTime(LocalDateTime.now());
            profileRepository.save(entity);
            profileRoleService.create(entity, ProfileRole.ROLE_USER);
            smsSendingService.sendSms(dto.getUsername(), "Tasdiqlash kodi: " + otp);

            resultMessage = "Registration successful. Please enter the OTP sent to your phone number.";
        }

        return resultMessage;
    }

    @Transactional
    public String verifyEmail(String token) {
        try {
            Claims claims = jwtUtil.extractClaims(token);

            String tokenType = claims.get("type", String.class);
            if (!"EMAIL_VERIFICATION".equals(tokenType)) {
                throw new AppBadException("Invalid token type");
            }

            Integer profileId = claims.get("profileId", Integer.class);
            String username = claims.getSubject();

            ProfileEntity profileEntity = profileService.get(profileId);

            if (!profileEntity.getUsername().equals(username)) {
                throw new AppBadException("Invalid token");
            }

            if (profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
                return "Email already verified";
            }

            if (!profileEntity.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
                throw new AppBadException("Invalid profile status");
            }

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
    public String verifySms(SmsVerificationDTO dto) {

        ProfileEntity profileEntity = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername())
                .orElseThrow(() -> new AppBadException("User not found"));

        if (profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
            return "Profile already verified";
        }

        if (!profileEntity.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
            throw new AppBadException("Invalid profile status");
        }

        if (profileEntity.getSmsCodeGeneratedTime() == null ||
                profileEntity.getSmsCodeGeneratedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new AppBadException("OTP expired or not sent");
        }

        if (!profileEntity.getSmsCode().equals(dto.getVerificationCode())) {
            throw new AppBadException("Invalid OTP");
        }

        profileEntity.setStatus(ProfileStatus.ACTIVE);
        profileEntity.setSmsCode(null); // Kodni tozalash
        profileRepository.save(profileEntity);

        return "Phone number verified successfully. You can now login.";
    }


    @Transactional
    public String verifyPhone(VerificationDTO dto) {

        ProfileEntity profileEntity = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername())
                .orElseThrow(() -> new AppBadException("User not found"));

        if (!profileEntity.getContactType().equals(ProfileContactType.PHONE)) {
            throw new AppBadException("Invalid verification method for this user.");
        }
        if (profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
            return "Phone number already verified";
        }
        if (!profileEntity.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
            throw new AppBadException("Invalid profile status");
        }

        if (profileEntity.getVerificationCodeGeneratedTime() == null ||
                profileEntity.getVerificationCodeGeneratedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new AppBadException("OTP expired or not sent");
        }

        if (!profileEntity.getVerificationCode().equals(dto.getCode())) {
            throw new AppBadException("Invalid OTP");
        }

        profileEntity.setStatus(ProfileStatus.ACTIVE);
        profileEntity.setVerificationCode(null); // Kodni tozalash
        profileRepository.save(profileEntity);

        return "Phone number verified successfully. You can now login.";
    }

    @Transactional
    public String resendVerificationEmail(String username) {
        ProfileEntity profileEntity = profileRepository.findByUsernameAndVisibleTrue(username)
                .orElseThrow(() -> new AppBadException("User not found"));

        if (profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
            throw new AppBadException("User is already verified");
        }

        if (!profileEntity.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
            throw new AppBadException("Invalid profile status");
        }

        String verificationToken = jwtUtil.generateEmailVerificationToken(
                profileEntity.getId(),
                profileEntity.getUsername()
        );

        String verifyUrl = frontendUrl + "/auth/verify?token=" + verificationToken;

        MessageDTO dto = new MessageDTO();
        dto.setToAccount(username);
        dto.setSubject("Email Verification - New Link");
        dto.setName(profileEntity.getName());
        dto.setVerifyUrl(verifyUrl);

        emailSendingService.sendMimeMessage(dto);

        return "New verification link sent to your email";
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {
        ProfileEntity profileEntity = profileRepository.findByUsernameAndVisibleTrue(loginDTO.getUsername())
                .orElseThrow(() -> new AppBadException("Login yoki parol noto'g'ri"));

        boolean matches = passwordEncoder.matches(loginDTO.getPassword(), profileEntity.getPassword());
        if (!matches) {
            throw new AppBadException("Login yoki parol noto'g'ri");
        }

        if (!profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
            if (profileEntity.getStatus().equals(ProfileStatus.REGISTRATION_PROGRESS)) {
                throw new AppBadException("Email tasdiqlanmagan. Iltimos, emailingizni tekshiring.");
            }
            throw new AppBadException("Sizning profilingiz faol emas.");
        }

        ProfileRole role = (ProfileRole) profileRoleService.getRole(profileEntity.getId())
                .orElseThrow(() -> new AppBadException("Profil role'lari topilmadi"));


        String authToken = jwtUtil.generateAccessToken(profileEntity.getId(), profileEntity.getUsername(), role);

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setName(profileEntity.getName());
        responseDTO.setUsername(profileEntity.getUsername());
        responseDTO.setRole(role.name());
        responseDTO.setToken(authToken);

        return responseDTO;
    }


}

