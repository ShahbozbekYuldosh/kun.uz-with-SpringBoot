package dasturlash.uz.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDTO {
    private Integer id;
    private String content;
    private String createdDate;
    private String updateDate;

    private ProfileDTO profile;
    private Integer articleId;

    private Integer likeCount;
    private Integer dislikeCount;
}
