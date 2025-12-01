package dasturlash.uz.config.security;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.entity.ProfileRoleEntity;
import dasturlash.uz.enums.ProfileRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    private final ProfileEntity profileEntity;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (profileEntity.getProfileRoles() == null || profileEntity.getProfileRoles().isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return profileEntity.getProfileRoles().stream()
                .map(pr -> new SimpleGrantedAuthority(pr.getRole().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return profileEntity.getPassword();
    }

    @Override
    public String getUsername() {
        // E-mail yoki username hisoblanadi
        return profileEntity.getUsername();
    }

    // Account holati tekshiruvlari
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Faqat ACTIVE profillar ishlashi uchun Status ni tekshirish
        return profileEntity.getStatus().name().equals("ACTIVE");
    }
}