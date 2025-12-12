package dasturlash.uz.service;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.entity.ArticleEntity;
import dasturlash.uz.entity.ArticleLikeEntity;
import dasturlash.uz.enums.Emotion;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ArticleLikeRepository;
import dasturlash.uz.repository.ArticleRepository;
import dasturlash.uz.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final JwtUtil jwtUtil;

    private Integer getCurrentUserId() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return jwtUtil.extractClaims(token).get("id", Integer.class);
    }

    public ApiResponse like(Integer articleId) {
        Integer profileId = getCurrentUserId();

        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new AppBadException("Article not found"));

        articleLikeRepository.findByProfileIdAndArticleId(profileId, articleId)
                .ifPresent(articleLikeRepository::delete);

        ArticleLikeEntity entity = new ArticleLikeEntity();
        entity.setArticle(article);
        entity.setProfileId(profileId);
        entity.setEmotion(Emotion.LIKE);
        entity.setCreatedDate(LocalDateTime.now());

        articleLikeRepository.save(entity);

        return new ApiResponse(true, "Liked");
    }

    public ApiResponse dislike(Integer articleId) {
        Integer profileId = getCurrentUserId();

        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new AppBadException("Article not found"));

        articleLikeRepository.findByProfileIdAndArticleId(profileId, articleId)
                .ifPresent(articleLikeRepository::delete);

        ArticleLikeEntity entity = new ArticleLikeEntity();
        entity.setArticle(article);
        entity.setProfileId(profileId);
        entity.setEmotion(Emotion.DISLIKE);
        entity.setCreatedDate(LocalDateTime.now());
        articleLikeRepository.save(entity);

        return new ApiResponse(true, "Disliked");
    }

    public ApiResponse remove(Integer articleId) {
        Integer profileId = getCurrentUserId();

        ArticleLikeEntity likeEntity = articleLikeRepository
                .findByProfileIdAndArticleId(profileId, articleId)
                .orElseThrow(() -> new AppBadException("Like/Dislike not found"));
        articleLikeRepository.delete(likeEntity);

        return new ApiResponse(true, "Removed");
    }
}
