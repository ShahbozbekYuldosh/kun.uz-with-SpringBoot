package dasturlash.uz.controller;

import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.CategoryService;
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
@RequestMapping("/api/v1/category")
@Tag(name = "Category API", description = "Categorylarni boshqarish API")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // -----------------------------------
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Category yaratish", description = "ADMIN tomonidan yangi category yaratadi")
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.create(dto));
    }

    // -----------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Category update", description = "Berilgan id bo‘yicha ADMIN category update qiladi")
    public ResponseEntity<CategoryDTO> update(@PathVariable Integer id, @Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    // -----------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Category delete", description = "Berilgan id bo‘yicha ADMIN category delete qiladi")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.delete(id));
    }

    // -----------------------------------
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "ADMIN Category List", description = "Barcha category ro‘yxati order_number bo‘yicha")
    public ResponseEntity<List<CategoryDTO>> all() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    // -----------------------------------
    @GetMapping("/lang")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Til bo‘yicha category list", description = "Accept-Language bo‘yicha category nomlarini qaytaradi")
    public ResponseEntity<List<CategoryDTO>> getByLang(
            @RequestHeader(name = "Accept-Language", defaultValue = "UZ")
            @Parameter(description = "UZ, RU, EN, KRILL tillaridan biri")
            AppLanguageEnum language
    ) {
        return ResponseEntity.ok(categoryService.getAllByLang(language));
    }
}

