package dasturlash.uz.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentShortDTO {
    private Integer id;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private ProfileSmallDTO profile;
    private String content;
    private Integer articleId;
    private Long likeCount;
    private Long dislikeCount;
}
