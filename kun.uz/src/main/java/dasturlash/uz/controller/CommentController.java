package dasturlash.uz.controller;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.dto.comment.CommentCreateDTO;
import dasturlash.uz.dto.comment.CommentUpdateDTO;
import dasturlash.uz.enums.Emotion;
import dasturlash.uz.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private String getRole() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }

    @Operation(summary = "Create comment (ANY)")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CommentCreateDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(commentService.create(dto)));
    }

    @Operation(summary = "Update comment (Owner)")
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody CommentUpdateDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(commentService.update(dto)));
    }

    @Operation(summary = "Delete Comment (ADMIN or Owner)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(commentService.delete(id, getRole())));
    }

    @Operation(summary = "Comment list by article id")
    @GetMapping("/article/{id}")
    public ResponseEntity<?> getComments(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getByArticleId(id)));
    }

    @Operation(summary = "Get replied comments")
    @GetMapping("/replies/{id}")
    public ResponseEntity<?> replies(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getReplies(id)));
    }

    @Operation(summary = "Like comment")
    @PostMapping("/like/{id}")
    public ResponseEntity<?> like(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(commentService.like(id, Emotion.LIKE)));
    }

    @Operation(summary = "Dislike comment")
    @PostMapping("/dislike/{id}")
    public ResponseEntity<?> dislike(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(commentService.like(id, Emotion.DISLIKE)));
    }
}
