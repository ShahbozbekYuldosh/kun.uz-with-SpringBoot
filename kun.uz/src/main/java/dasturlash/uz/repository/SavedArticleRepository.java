package dasturlash.uz.repository;

import dasturlash.uz.entity.SavedArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedArticleRepository extends JpaRepository<SavedArticleEntity, Integer> {
    Optional<SavedArticleEntity> findByProfileIdAndArticleId(Integer profileId, Integer articleId);
    List<SavedArticleEntity> findAllByProfileIdOrderByCreatedDateDesc(Integer profileId);
}
