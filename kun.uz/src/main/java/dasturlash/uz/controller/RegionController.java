package dasturlash.uz.controller;

import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/region")
@Tag(name = "Region API", description = "Regionlarni boshqarish uchun API")
public class RegionController {

    private final RegionService regionService;

    // -------------------------------------------------------
    @Operation(
            summary = "Region yaratish",
            description = "Bu endpoint faqat ADMIN tomonidan chaqiriladi va yangi region yaratadi."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Muvoffaqiyatli yaratildi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Notog'ri ma'lumot yuborilgan"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / Token kerak"),
            @ApiResponse(responseCode = "403", description = "Admin emas")
    })
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegionDTO> create(@Valid @RequestBody RegionDTO dto) {
        return ResponseEntity.ok(regionService.create(dto));
    }

    // -------------------------------------------------------
    @Operation(
            summary = "Region ma'lumotlarini o‘zgartirish",
            description = "Berilgan ID bo‘yicha regionni yangilaydi. Faqat ADMIN foydalanadi."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Muvoffaqiyatli yangilandi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Region topilmadi"),
            @ApiResponse(responseCode = "400", description = "Xato ma'lumot"),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegionDTO> update(@PathVariable("id") Integer id,
                                            @RequestBody RegionDTO newDto) {
        return ResponseEntity.ok(regionService.update(id, newDto));
    }

    // -------------------------------------------------------
    @Operation(
            summary = "Region o‘chirish",
            description = "ID bo‘yicha regionni o‘chiradi (visible=false). Faqat ADMIN foydalanadi."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Muvoffaqiyatli o‘chirildi"),
            @ApiResponse(responseCode = "404", description = "Region topilmadi")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(regionService.delete(id));
    }

    // -------------------------------------------------------
    @Operation(
            summary = "ADMIN uchun regionlar ro'yxati",
            description = "Barcha regionlar ro'yxatini (visible=true) admin uchun qaytaradi."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegionDTO>> all() {
        return ResponseEntity.ok(regionService.getAll());
    }

    // -------------------------------------------------------
    @Operation(
            summary = "Til bo‘yicha regionlar ro‘yxati",
            description = "Accept-Language orqali (UZ, RU, EN, KRILL) region nomlarini mos ravishda qaytaradi."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    @GetMapping("/lang")
    public ResponseEntity<List<RegionDTO>> getByLang(
            @Parameter(
                    name = "Accept-Language",
                    example = "UZ",
                    description = "UZ, RU, EN, KRILL tillaridan biri"
            )
            @RequestHeader(name = "Accept-Language", defaultValue = "UZ") AppLanguageEnum language) {
        return ResponseEntity.ok(regionService.getAllByLang(language));
    }
}