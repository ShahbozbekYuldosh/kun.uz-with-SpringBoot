package dasturlash.uz.controller;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.PaginationResponseDTO;
import dasturlash.uz.service.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attach")
@RequiredArgsConstructor
public class AttachController {

    private final AttachService attachService;

    // --- 1. UPLOAD (ANY) ---
    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> upload(@RequestParam("file") MultipartFile file) {
        AttachDTO dto = attachService.upload(file);
        return ResponseEntity.ok(dto);
    }

    // --- 2. OPEN (ANY) ---
    // Fayl ID orqali brauzerda ko'rish
    @GetMapping("/open/{id}")
    public ResponseEntity<Resource> open(@PathVariable("id") String id) {
        // AttachService.open() metodi Resource va ContentType'ni qaytaradi
        return attachService.open(id);
    }

    // --- 3. DOWNLOAD (ANY) ---
    // Fayl ID orqali yuklab olish
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") String id) {
        // AttachService.download() metodi Resource va Content-Disposition headerini qaytaradi
        return attachService.download(id);
    }

    // --- 4. PAGINATION (ADMIN) ---
    @GetMapping("/adm/pagination")
    public ResponseEntity<PaginationResponseDTO<AttachDTO>> getPagination(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
            /* HttpServletRequest orqali ADMIN rolini tekshirish lozim */) {

        // Agar xavfsizlik (Security) ishlatilmasa, yuqoridagi kommentariy o'rnida
        // JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN) kabi tekshiruv bo'lishi kerak.

        PaginationResponseDTO<AttachDTO> response = attachService.getPagination(page, size);
        return ResponseEntity.ok(response);
    }

    // --- 5. DELETE (ADMIN) ---
    @DeleteMapping("/adm/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id
            /* HttpServletRequest request */) {

        // ADMIN rolini tekshirish shart

        String response = attachService.delete(id);
        return ResponseEntity.ok(response);
    }
}