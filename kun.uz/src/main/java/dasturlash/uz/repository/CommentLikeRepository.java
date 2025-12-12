package dasturlash.uz.repository;

import dasturlash.uz.entity.CommentLikeEntity;
import dasturlash.uz.enums.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Integer> {

    Optional<CommentLikeEntity> findByProfileIdAndCommentId(Integer profileId, Integer commentId);

    Integer countByCommentIdAndEmotion(Integer commentId, Emotion emotion);
}