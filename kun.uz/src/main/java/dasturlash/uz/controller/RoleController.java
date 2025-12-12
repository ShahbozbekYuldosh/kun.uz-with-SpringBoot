package dasturlash.uz.controller;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.dto.RoleChangeDTO;
import dasturlash.uz.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Admin uchun role o'zgartirish API-lari")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Profile rolini o'zgartirish (faqat ADMIN)")
    @PostMapping("/change")
    public ResponseEntity<ApiResponse<Void>> changeRole(@RequestBody RoleChangeDTO dto) {
        roleService.changeUserRole(dto);
        return ResponseEntity.ok(ApiResponse.successMessage("Role muvaffaqiyatli o'zgartirildi"));
    }
}
