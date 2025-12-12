package dasturlash.uz.controller;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.dto.sms.SmsHistoryDTO;
import dasturlash.uz.service.SmsHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
@Tag(name = "SMS History", description = "SMS yuborish tarixini boshqarish va ko'rish")
public class SmsHistoryController {

    private final SmsHistoryService smsHistoryService;

    @Operation(summary = "Phone bo'yicha SMS history")
    @GetMapping("/history/phone/{phone}")
    public ResponseEntity<ApiResponse<List<SmsHistoryDTO>>> getByPhone(@PathVariable String phone) {
        List<SmsHistoryDTO> list = smsHistoryService.getByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @Operation(summary = "Sana oralig'iga ko'ra SMS history (from..to)")
    @GetMapping("/history/date")
    public ResponseEntity<ApiResponse<Page<SmsHistoryDTO>>> getByDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SmsHistoryDTO> pageResult = smsHistoryService.getByDateRange(from, to, page, size);
        return ResponseEntity.ok(ApiResponse.success(pageResult));
    }

    @Operation(summary = "Admin uchun SMS history pagination")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/history/admin")
    public ResponseEntity<ApiResponse<Page<SmsHistoryDTO>>> paginationForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SmsHistoryDTO> pageResult = smsHistoryService.paginationForAdmin(page, size);
        return ResponseEntity.ok(ApiResponse.success(pageResult));
    }
}
