package dasturlash.uz.controller;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.entity.SavedArticleEntity;
import dasturlash.uz.service.SavedArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-articles")
@RequiredArgsConstructor
@Tag(name = "Saved Article", description = "Saved Article CRUD operations")
public class SavedArticleController {

    private final SavedArticleService savedArticleService;

    @PostMapping("/create/{articleId}")
    @Operation(summary = "Save an article for the current user")
    public ResponseEntity<ApiResponse> create(@PathVariable Integer articleId) {
        return ResponseEntity.ok(savedArticleService.create(articleId));
    }

    @DeleteMapping("/delete/{articleId}")
    @Operation(summary = "Remove saved article for the current user")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer articleId) {
        return ResponseEntity.ok(savedArticleService.delete(articleId));
    }

    @GetMapping("/list")
    @Operation(summary = "Get saved articles for the current user")
    public ResponseEntity<List<SavedArticleEntity>> list() {
        return ResponseEntity.ok(savedArticleService.getSavedArticles());
    }
}
