package dasturlash.uz.dto.article;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleShortInfoDTO {
    private Integer id;
    private String title;
    private String description;
    private Integer imageId;
    private String imageUrl;
    private LocalDateTime publishedDate;
}