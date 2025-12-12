package dasturlash.uz.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentFilterDTO {
    private Integer id;
    private LocalDateTime createdDateFrom;
    private LocalDateTime createdDateTo;
    private Integer profileId;
    private Integer articleId;
}
