package dasturlash.uz.controller;


import dasturlash.uz.config.security.CustomUserDetails;
import dasturlash.uz.dto.profile.*;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getProfileEntity().getId();
        }
        throw new RuntimeException("Autentifikatsiya ma'lumotlari topilmadi.");
    }

    // 1. Create profile (ADMIN)
    @PostMapping("/admin")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProfileDTO> create(@Valid @RequestBody ProfileCreateDTO dto) {
        Integer adminId = getCurrentUserId();
        ProfileDTO response = profileService.create(dto, adminId);
        return ResponseEntity.ok(response);
    }

    // 2. Get By Id. (ADMIN)
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDTO> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(profileService.getById(id));
    }

    // 3. Update Profile (ADMIN)
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDTO> updateByAdmin(@PathVariable("id") Integer targetId,
                                                        @Valid @RequestBody ProfileUpdateByAdminDTO dto) {
        ProfileDTO response = profileService.updateByAdmin(targetId, dto);
        return ResponseEntity.ok(response);
    }

    // 4. Update Profile Detail (ANY) - Foydalanuvchi o'zini o'zgartiradi
    @PutMapping("/detail")
    @PreAuthorize("isAuthenticated()") // Barcha kirgan foydalanuvchilar
    public ResponseEntity<ProfileDTO> updateDetail(@Valid @RequestBody ProfileDetailUpdateDTO dto) {
        Integer currentUserId = getCurrentUserId();
        ProfileDTO response = profileService.updateDetail(currentUserId, dto);
        return ResponseEntity.ok(response);
    }

    // 5. Profile List (ADMIN) (Pagination)
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProfileDTO>> getList(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<ProfileDTO> result = profileService.getList(page, size);
        return ResponseEntity.ok(result);
    }

    // 6. Delete Profile By Id (ADMIN)
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        profileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 7. Update Photo (ANY)
    @PutMapping("/photo") // URI ni tozalaymiz
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePhoto(@Valid @RequestBody PhotoUpdateDTO dto) {
        Integer currentUserId = getCurrentUserId();
        profileService.updatePhoto(currentUserId, dto.getAttachId());
        return ResponseEntity.ok().build();
    }

    // 8. Update Password (ANY)
    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        Integer currentUserId = getCurrentUserId();
        profileService.updatePassword(currentUserId, dto.getOldPassword(), dto.getNewPassword());

        return ResponseEntity.ok().build();
    }

    // 9. Filter (ADMIN)
    @GetMapping("/admin/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProfileDTO>> filter(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) ProfileRole role,
            @RequestParam(required = false) LocalDateTime createdFrom,
            @RequestParam(required = false) LocalDateTime createdTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProfileDTO> result = profileService.filter(query, role, createdFrom, createdTo, page, size);
        return ResponseEntity.ok(result);
    }
}