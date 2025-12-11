package dasturlash.uz.controller;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.PaginationResponseDTO;
import dasturlash.uz.service.AttachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attach")
@RequiredArgsConstructor
@Tag(name = "Attach", description = "Fayl yuklash, ochish, yuklab olish, sahifalash va o‘chirish amallari")
public class AttachController {

    private final AttachService attachService;

    // --------------------------- UPLOAD ---------------------------
    @Operation(summary = "Fayl yuklash", description = "Faylni serverga yuklash. Har qanday autentifikatsiyadan o‘tgan foydalanuvchi ishlata oladi")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> upload(@RequestParam("file") MultipartFile file) {
        AttachDTO dto = attachService.upload(file);
        return ResponseEntity.ok(dto);
    }

    // --------------------------- OPEN ---------------------------
    @Operation(summary = "Faylni ochish", description = "Faylni ID orqali ochish, fayl resursini qaytaradi")
    @GetMapping("/open/{id}")
    public ResponseEntity<Resource> open(@PathVariable String id) {
        return attachService.open(id);
    }

    // --------------------------- DOWNLOAD ---------------------------
    @Operation(summary = "Faylni yuklab olish", description = "Faylni ID orqali original nom bilan yuklab olish")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id) {
        return attachService.download(id);
    }

    // --------------------------- PAGINATION ---------------------------
    @Operation(summary = "Fayllarni sahifalash", description = "ADMIN foydalanuvchilar uchun sahifalangan fayllar ro‘yxati")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pagination")
    public ResponseEntity<PaginationResponseDTO<AttachDTO>> pagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PaginationResponseDTO<AttachDTO> result = attachService.pagination(page, size);
        return ResponseEntity.ok(result);
    }

    // --------------------------- DELETE ---------------------------
    @Operation(summary = "Faylni o‘chirish", description = "Faylni tizimdan va ma’lumotlar bazasidan o‘chirish. Faqat ADMIN foydalanuvchilar")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        String message = attachService.delete(id);
        return ResponseEntity.ok(message);
    }
}
