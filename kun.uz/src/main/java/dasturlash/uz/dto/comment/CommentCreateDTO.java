package dasturlash.uz.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Comment yaratish DTO")
public class CommentCreateDTO {
    @Schema(description = "Maqola id", example = "1")
    private Integer articleId;

    @Schema(description = "Reply qilingan comment id (optional)", example = "5")
    private Integer replyId;

    @Schema(description = "Comment matni", example = "Juda yaxshi maqola!")
    private String content;
}
