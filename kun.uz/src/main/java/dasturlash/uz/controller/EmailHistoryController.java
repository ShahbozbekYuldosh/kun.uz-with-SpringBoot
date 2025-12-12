package dasturlash.uz.controller;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.dto.EmailHistoryDTO;
import dasturlash.uz.service.EmailHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/email-history")
@RequiredArgsConstructor
@Tag(
        name = "Email Tarix API",
        description = "Tizim tomonidan yuborilgan email xabarlari tarixini boshqarish"
)
public class EmailHistoryController {

    private final EmailHistoryService service;

    //--------------------- 1. Email bo'yicha tarix ------------------------
    @Operation(summary = "Email bo‘yicha yuborilgan xabarlar tarixini olish")
    @GetMapping("/email/{email}")
    public ApiResponse<List<EmailHistoryDTO>> getByEmail(@PathVariable String email) {
        return ApiResponse.success(service.getByEmail(email));
    }

    //-------------------- 2. Sana oralig'i bo'yicha tarix ------------------
    @Operation(summary = "Berilgan sana oralig‘idagi email tarixini olish")
    @GetMapping("/date")
    public ApiResponse<List<EmailHistoryDTO>> getByDate(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ApiResponse.success(service.getByDate(from, to));
    }

    //------------------- 3. ADMIN – pagination --------------------------
    @Operation(summary = "ADMIN: Email tarixining sahifalangan ro‘yxatini olish")
    @GetMapping("/admin")
    public ApiResponse<Page<EmailHistoryDTO>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(service.getAdminPage(page, size));
    }
}
