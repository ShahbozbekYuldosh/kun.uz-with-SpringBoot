package dasturlash.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @Column(name = "profile_id", insertable = false, updatable = false)
    private Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "article_id", insertable = false, updatable = false)
    private Integer articleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @Column(name = "reply_id")
    private Integer replyId; // self-referential

    @OneToMany(mappedBy = "replyId", fetch = FetchType.LAZY)
    private List<CommentEntity> replies;

    private Boolean visible = Boolean.TRUE;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
    private List<CommentLikeEntity> likes;
}
