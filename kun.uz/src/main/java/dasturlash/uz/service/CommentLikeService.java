package dasturlash.uz.service;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.entity.CommentEntity;
import dasturlash.uz.entity.CommentLikeEntity;
import dasturlash.uz.enums.Emotion;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.CommentLikeRepository;
import dasturlash.uz.repository.CommentRepository;
import dasturlash.uz.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final JwtUtil jwtUtil;

    private Integer getCurrentUserId() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return jwtUtil.extractClaims(token).get("id", Integer.class);
    }

    // Comment Like
    public ApiResponse like(Integer commentId) {
        Integer profileId = getCurrentUserId();

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppBadException("Comment not found"));

        // Avvalgi Like/Dislike mavjud bo'lsa o'chirish
        commentLikeRepository.findByProfileIdAndCommentId(profileId, commentId)
                .ifPresent(commentLikeRepository::delete);

        CommentLikeEntity entity = new CommentLikeEntity();
        entity.setComment(comment);
        entity.setProfileId(profileId);
        entity.setEmotion(Emotion.LIKE);
        entity.setCreatedDate(LocalDateTime.now());

        commentLikeRepository.save(entity);

        return ApiResponse.success("Comment liked successfully");
    }

    // Comment Dislike
    public ApiResponse dislike(Integer commentId) {
        Integer profileId = getCurrentUserId();

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppBadException("Comment not found"));

        commentLikeRepository.findByProfileIdAndCommentId(profileId, commentId)
                .ifPresent(commentLikeRepository::delete);

        CommentLikeEntity entity = new CommentLikeEntity();
        entity.setComment(comment);
        entity.setProfileId(profileId);
        entity.setEmotion(Emotion.DISLIKE);
        entity.setCreatedDate(LocalDateTime.now());

        commentLikeRepository.save(entity);

        return ApiResponse.success("Comment disliked successfully");
    }

    // Comment Like/Dislike remove
    public ApiResponse remove(Integer commentId) {
        Integer profileId = getCurrentUserId();

        CommentLikeEntity likeEntity = commentLikeRepository
                .findByProfileIdAndCommentId(profileId, commentId)
                .orElseThrow(() -> new AppBadException("Like/Dislike not found"));

        commentLikeRepository.delete(likeEntity);

        return ApiResponse.success("Comment like/dislike removed successfully");
    }
}
