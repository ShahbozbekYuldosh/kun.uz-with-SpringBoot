package dasturlash.uz.controller;

import dasturlash.uz.dto.SectionDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/section")
@Tag(name = "Section API", description = "Sectionlarni boshqarish API")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Section yaratish", description = "ADMIN tomonidan yangi section yaratadi")
    public ResponseEntity<SectionDTO> create(@Valid @RequestBody SectionDTO dto) {
        return ResponseEntity.ok(sectionService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Section update", description = "Berilgan id bo‘yicha ADMIN section update qiladi")
    public ResponseEntity<SectionDTO> update(@PathVariable Integer id, @Valid @RequestBody SectionDTO dto) {
        return ResponseEntity.ok(sectionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Section delete", description = "Berilgan id bo‘yicha ADMIN section delete qiladi")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(sectionService.delete(id));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "ADMIN Section List", description = "Barcha section ro‘yxati pagination bilan")
    public ResponseEntity<List<SectionDTO>> all() {
        return ResponseEntity.ok(sectionService.getAll());
    }

    @GetMapping("/lang")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Til bo‘yicha section list", description = "Accept-Language bo‘yicha section nomlarini qaytaradi")
    public ResponseEntity<List<SectionDTO>> getByLang(
            @RequestHeader(name = "Accept-Language", defaultValue = "UZ")
            @Parameter(description = "UZ, RU, EN tillaridan biri")
            AppLanguageEnum language
    ) {
        return ResponseEntity.ok(sectionService.getAllByLang(language));
    }
}
