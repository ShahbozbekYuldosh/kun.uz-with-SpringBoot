package dasturlash.uz.entity;

import dasturlash.uz.enums.Emotion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_like")
@Getter
@Setter
public class CommentLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "profile_id", insertable = false, updatable = false)
    private Integer profileId;

    @Column(name = "comment_id", insertable = false, updatable = false)
    private Integer commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;

    @Enumerated(EnumType.STRING)
    private Emotion emotion; // LIKE or DISLIKE

    private LocalDateTime createdDate;
}
