package dasturlash.uz.controller;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.service.CommentLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment-like")
@RequiredArgsConstructor
@Tag(name = "Comment Like API", description = "Comment ga Like/Dislike va Remove qilish endpointlari")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @Operation(
            summary = "Comment Like qilish",
            description = "Berilgan comment_id uchun Like qo'shadi",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Comment muvaffaqiyatli Like qilindi",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Comment topilmadi yoki xato")
            }
    )
    @PostMapping("/like")
    public ResponseEntity<ApiResponse> like(
            @RequestParam(name = "commentId") Integer commentId) {
        return ResponseEntity.ok(commentLikeService.like(commentId));
    }

    @Operation(
            summary = "Comment Dislike qilish",
            description = "Berilgan comment_id uchun Dislike qo'shadi",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Comment muvaffaqiyatli Dislike qilindi",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Comment topilmadi yoki xato")
            }
    )
    @PostMapping("/dislike")
    public ResponseEntity<ApiResponse> dislike(
            @RequestParam(name = "commentId") Integer commentId) {
        return ResponseEntity.ok(commentLikeService.dislike(commentId));
    }

    @Operation(
            summary = "Comment Like/Dislike ni olib tashlash",
            description = "Berilgan comment_id uchun Like yoki Dislike ni olib tashlaydi",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Comment Like/Dislike muvaffaqiyatli o'chirildi",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Like/Dislike topilmadi")
            }
    )
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse> remove(
            @RequestParam(name = "commentId") Integer commentId) {
        return ResponseEntity.ok(commentLikeService.remove(commentId));
    }
}
