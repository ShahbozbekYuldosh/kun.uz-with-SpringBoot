package dasturlash.uz.config;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.service.ProfileRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final ProfileRepository profileRepository;
    private final ProfileRoleService profileRoleService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!profileRoleService.existsByRole(ProfileRole.ROLE_ADMIN)) {

            if (!profileRepository.existsByUsername("superadmin@dasturlash.uz")) {
                ProfileEntity admin = new ProfileEntity();
                admin.setUsername("superadmin@dasturlash.uz");
                admin.setPassword(passwordEncoder.encode("1234"));
                admin.setName("Super Admin");
                profileRepository.save(admin);
            profileRoleService.create(admin, ProfileRole.ROLE_ADMIN);
            }
            System.out.println(">>> BIRINCHI SUPER ADMIN MUVAFFFAQIYATLI YARATILDI! <<<");
        }
    }
}