package dasturlash.uz.repository;

import dasturlash.uz.entity.ArticleLikeEntity;
import dasturlash.uz.enums.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLikeEntity, Integer> {

    Optional<ArticleLikeEntity> findByProfileIdAndArticleId(Integer profileId, Integer articleId);

    long countByArticleIdAndEmotion(Integer articleId, Emotion emotion);
}
