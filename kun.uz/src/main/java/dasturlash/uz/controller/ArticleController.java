package dasturlash.uz.controller;

import dasturlash.uz.dto.article.*;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ArticleStatusEnum;
import dasturlash.uz.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // --------------------------- CREATE ---------------------------
    @Operation(summary = "Maqola yaratish (Moderator)", description = "Moderator maqolani NOT_PUBLISHED statusda yaratadi")
    @PostMapping("/create")
    @PreAuthorize("hasRole('MODERATOR')")
    public ArticleInfoDTO createArticle(@RequestBody ArticleCreateDTO dto,
                                        @RequestAttribute("profile") ProfileEntity moderator) {
        return articleService.createArticle(dto, moderator);
    }

    // --------------------------- UPDATE ---------------------------
    @Operation(summary = "Maqola yangilash (Moderator)", description = "Moderator maqolani NOT_PUBLISHED statusga yangilaydi")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ArticleInfoDTO updateArticle(@PathVariable Integer id,
                                        @RequestBody ArticleUpdateDTO dto,
                                        @RequestAttribute("profile") ProfileEntity moderator) {
        return articleService.updateArticle(id, dto, moderator);
    }

    // --------------------------- DELETE ---------------------------
    @Operation(summary = "Maqolani o'chirish (Moderator)")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public String deleteArticle(@PathVariable Integer id) {
        return articleService.deleteArticle(id);
    }

    // --------------------------- CHANGE STATUS ---------------------------
    @Operation(summary = "Maqola statusini o'zgartirish (Publisher)")
    @PostMapping("/change-status/{id}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public String changeStatus(@PathVariable Integer id,
                               @RequestParam ArticleStatusEnum status,
                               @RequestAttribute("profile") ProfileEntity publisher) {
        return articleService.changeStatus(id, status, publisher);
    }


    // --------------------------- LAST N ARTICLES BY SECTION ---------------------------
    @Operation(summary = "Oxirgi N maqola by SectionId", description = "Berilgan Section bo'yicha oxirgi N ta PUBLISHED maqolalarni qaytaradi")
    @GetMapping("/last-by-section/{sectionId}")
    public List<ArticleShortInfoDTO> getLastNBySection(@PathVariable Integer sectionId,
                                                       @RequestParam int size) {
        return articleService.getLastNBySection(sectionId, size);
    }

    // --------------------------- LAST 12 EXCEPT ---------------------------
    @Operation(summary = "Oxirgi 12 maqola (berilgan listdan tashqari)")
    @GetMapping("/last-12-except")
    public List<ArticleShortInfoDTO> getLast12Except(@RequestParam List<Integer> excludeIds) {
        return articleService.getLast12Except(excludeIds);
    }

    // --------------------------- LAST N BY CATEGORY ---------------------------
    @Operation(summary = "Oxirgi N maqola by CategoryId")
    @GetMapping("/last-by-category/{categoryId}")
    public List<ArticleShortInfoDTO> getLastNByCategory(@PathVariable Integer categoryId,
                                                        @RequestParam int size) {
        return articleService.getLastNByCategory(categoryId, size);
    }

    // --------------------------- LAST N BY REGION ---------------------------
    @Operation(summary = "Oxirgi N maqola by RegionId")
    @GetMapping("/last-by-region/{regionId}")
    public List<ArticleFullInfoDTO> getLastNByRegion(@PathVariable Integer regionId,
                                                     @RequestParam int size) {
        return articleService.getLastNByRegion(regionId, size);
    }

    // --------------------------- ARTICLE BY ID & LANG ---------------------------
    @Operation(summary = "Maqola by Id va Language")
    @GetMapping("/{id}")
    public ArticleFullInfoDTO getArticleById(@PathVariable Integer id,
                                             @RequestParam(defaultValue = "UZ") String lang) {
        return articleService.getArticleByIdAndLang(id, lang);
    }

    // --------------------------- INCREASE VIEW COUNT ---------------------------
    @Operation(summary = "Maqola ko‘rish count oshirish")
    @PostMapping("/increase-view/{id}")
    public Integer increaseViewCount(@PathVariable Integer id) {
        return articleService.increaseViewCount(id);
    }

    // --------------------------- INCREASE SHARE COUNT ---------------------------
    @Operation(summary = "Maqola share count oshirish")
    @PostMapping("/increase-share/{id}")
    public Integer increaseShareCount(@PathVariable Integer id) {
        return articleService.increaseShareCount(id);
    }

    // --------------------------- FILTER ARTICLE FOR PUBLISHER ---------------------------
    @Operation(summary = "Filter article (Publisher)")
    @PostMapping("/filter/publisher")
    @PreAuthorize("hasRole('PUBLISHER')")
    public List<ArticleShortInfoDTO> filterPublisher(@RequestBody ArticleFilterDTO filter) {
        return articleService.filterPublisher(filter);
    }

    // --------------------------- FILTER ARTICLE FOR MODERATOR ---------------------------
    @Operation(summary = "Filter article (Moderator)")
    @PostMapping("/filter/moderator")
    @PreAuthorize("hasRole('MODERATOR')")
    public List<ArticleShortInfoDTO> filterModerator(@RequestBody ArticleFilterDTO filter,
                                                     @RequestAttribute("profile") ProfileEntity moderator) {
        return articleService.filterModerator(filter, moderator);
    }

    // --------------------------- FILTER ARTICLE FOR ADMIN ---------------------------
    @Operation(summary = "Filter article (Admin)")
    @PostMapping("/filter/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ArticleShortInfoDTO> filterAdmin(@RequestBody ArticleFilterDTO filter) {
        return articleService.filterAdmin(filter);
    }

}
