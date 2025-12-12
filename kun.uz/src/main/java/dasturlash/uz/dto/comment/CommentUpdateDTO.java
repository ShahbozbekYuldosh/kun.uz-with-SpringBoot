package dasturlash.uz.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Comment yangilash DTO")
public class CommentUpdateDTO {
    @Schema(description = "Maqola id", example = "1")
    private Integer commentId;

    @Schema(description = "Comment matni", example = "Yangilangan matn")
    private String content;
}
