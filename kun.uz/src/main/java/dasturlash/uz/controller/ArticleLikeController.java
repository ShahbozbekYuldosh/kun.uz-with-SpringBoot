package dasturlash.uz.controller;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.service.ArticleLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article-like")
@RequiredArgsConstructor
@Tag(name = "Article Like API", description = "Articlega like/dislike berish APIlari")
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    // ===================== LIKE =====================
    @PostMapping("/like/{articleId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','MODERATOR','PUBLISHER')")
    @Operation(summary = "Like article", description = "Foydalanuvchi maqolaga LIKE qo'yadi.")
    public ResponseEntity<ApiResponse> like(@PathVariable Integer articleId) {
        return ResponseEntity.ok(articleLikeService.like(articleId));
    }

    // ===================== DISLIKE =====================
    @PostMapping("/dislike/{articleId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','MODERATOR','PUBLISHER')")
    @Operation(summary = "Dislike article", description = "Foydalanuvchi maqolaga DISLIKE qo'yadi.")
    public ResponseEntity<ApiResponse> dislike(@PathVariable Integer articleId) {
        return ResponseEntity.ok(articleLikeService.dislike(articleId));
    }

    // ===================== REMOVE =====================
    @DeleteMapping("/remove/{articleId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','MODERATOR','PUBLISHER')")
    @Operation(summary = "Remove like/dislike", description = "Foydalanuvchi like/dislike ni olib tashlaydi.")
    public ResponseEntity<ApiResponse> remove(@PathVariable Integer articleId) {
        return ResponseEntity.ok(articleLikeService.remove(articleId));
    }
}
