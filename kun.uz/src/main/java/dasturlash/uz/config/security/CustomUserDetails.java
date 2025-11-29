package dasturlash.uz.config.security;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.entity.ProfileRoleEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    private final ProfileEntity profileEntity;
    private final ProfileRoleEntity profileRoleEntity;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Rolni olish va uni Spring Security tushunadigan formatga (GrantedAuthority) o'tkazish
        // Sizning yechimingizda rol ProfileRoleService orqali olinishi kerak edi.
        // Hozircha oddiyroq qilib, faqat USER rolini qaytarib turamiz.
        // Keyinchalik bu yerga ProfileRoleService dan haqiqiy rol kiritiladi
        return Collections.singletonList(new SimpleGrantedAuthority(
                profileRoleEntity.getRole().name()
        ));
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