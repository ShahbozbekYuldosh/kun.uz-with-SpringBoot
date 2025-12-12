package dasturlash.uz.service;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.entity.ArticleEntity;
import dasturlash.uz.entity.SavedArticleEntity;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ArticleRepository;
import dasturlash.uz.repository.SavedArticleRepository;
import dasturlash.uz.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedArticleService {

    private final SavedArticleRepository savedArticleRepository;
    private final ArticleRepository articleRepository;
    private final JwtUtil jwtUtil;

    private Integer getCurrentUserId() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return jwtUtil.extractClaim(token, claims -> claims.get("id", Integer.class));
    }

    public ApiResponse create(Integer articleId) {
        Integer profileId = getCurrentUserId();

        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new AppBadException("Article not found"));

        if (savedArticleRepository.findByProfileIdAndArticleId(profileId, articleId).isPresent()) {
            return ApiResponse.of(false, "Article already saved", null);
        }

        SavedArticleEntity savedArticle = new SavedArticleEntity();
        savedArticle.setArticle(article);
        savedArticle.setProfileId(profileId);
        savedArticle.setCreatedDate(LocalDateTime.now());

        savedArticleRepository.save(savedArticle);
        return ApiResponse.of(true, "Article saved", null);
    }

    public ApiResponse delete(Integer articleId) {
        Integer profileId = getCurrentUserId();

        SavedArticleEntity savedArticle = savedArticleRepository
                .findByProfileIdAndArticleId(profileId, articleId)
                .orElseThrow(() -> new AppBadException("Saved article not found"));

        savedArticleRepository.delete(savedArticle);
        return ApiResponse.of(true, "Saved article removed", null);
    }

    public List<SavedArticleEntity> getSavedArticles() {
        Integer profileId = getCurrentUserId();
        return savedArticleRepository.findAllByProfileIdOrderByCreatedDateDesc(profileId);
    }
}
