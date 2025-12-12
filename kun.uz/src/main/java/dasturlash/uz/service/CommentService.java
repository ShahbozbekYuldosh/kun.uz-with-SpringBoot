package dasturlash.uz.service;

import dasturlash.uz.dto.comment.CommentCreateDTO;
import dasturlash.uz.dto.comment.CommentResponseDTO;
import dasturlash.uz.dto.comment.CommentUpdateDTO;
import dasturlash.uz.dto.comment.ProfileDTO;
import dasturlash.uz.entity.CommentEntity;
import dasturlash.uz.entity.CommentLikeEntity;
import dasturlash.uz.enums.Emotion;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.CommentLikeRepository;
import dasturlash.uz.repository.CommentRepository;
import dasturlash.uz.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final JwtUtil jwtUtil;

    private Integer getCurrentUserId() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof Integer id) {
            return id;
        }
        throw new AppBadException("User not authenticated");
    }

    // CREATE COMMENT
    public CommentResponseDTO create(CommentCreateDTO dto) {
        CommentEntity entity = new CommentEntity();
        entity.setContent(dto.getContent());
        entity.setArticleId(dto.getArticleId());
        entity.setReplyId(dto.getReplyId());
        entity.setProfileId(getCurrentUserId());
        entity.setCreatedDate(LocalDateTime.now());

        commentRepository.save(entity);
        return toDTO(entity);
    }

    // UPDATE COMMENT
    @Transactional
    public Boolean update(CommentUpdateDTO dto) {
        CommentEntity entity = commentRepository.findById(dto.getCommentId())
                .orElseThrow(() -> new AppBadException("Comment not found"));

        if (!entity.getProfileId().equals(getCurrentUserId())) {
            throw new AppBadException("You are not the owner!");
        }

        entity.setContent(dto.getContent());
        entity.setUpdateDate(LocalDateTime.now());
        return true;
    }

    // DELETE COMMENT
    public Boolean delete(Integer id, String role) {
        CommentEntity entity = commentRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Comment not found"));

        if (!role.equals("ADMIN") &&
                !entity.getProfileId().equals(getCurrentUserId())) {
            throw new AppBadException("Access denied");
        }

        entity.setVisible(false);
        commentRepository.save(entity);
        return true;
    }

    // LIST BY ARTICLE
    public java.util.List<CommentResponseDTO> getByArticleId(Integer articleId) {
        return commentRepository.findByArticleIdAndVisibleTrueOrderByCreatedDateDesc(articleId)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    // REPLIED COMMENTS
    public java.util.List<CommentResponseDTO> getReplies(Integer commentId) {
        return commentRepository.findByReplyId(commentId)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    // LIKE/DISLIKE
    @Transactional
    public Boolean like(Integer commentId, Emotion emotion) {
        Integer userId = getCurrentUserId();

        var optional = commentLikeRepository.findByProfileIdAndCommentId(userId, commentId);

        if (optional.isPresent()) {
            CommentLikeEntity like = optional.get();
            like.setEmotion(emotion);
            return true;
        }

        CommentLikeEntity like = new CommentLikeEntity();
        like.setCommentId(commentId);
        like.setProfileId(userId);
        like.setEmotion(emotion);
        like.setCreatedDate(LocalDateTime.now());

        commentLikeRepository.save(like);
        return true;
    }

    // DTO MAPPER
    private CommentResponseDTO toDTO(CommentEntity e) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(e.getId());
        dto.setContent(e.getContent());
        dto.setCreatedDate(String.valueOf(e.getCreatedDate()));
        dto.setUpdateDate(String.valueOf(e.getUpdateDate()));
        dto.setArticleId(e.getArticleId());

        if (e.getProfile() != null) {
            ProfileDTO p = new ProfileDTO();
            p.setId(e.getProfile().getId());
            p.setName(e.getProfile().getName());
            dto.setProfile(p);
        }

        dto.setLikeCount(commentLikeRepository.countByCommentIdAndEmotion(e.getId(), Emotion.LIKE));
        dto.setDislikeCount(commentLikeRepository.countByCommentIdAndEmotion(e.getId(), Emotion.DISLIKE));

        return dto;
    }
}
