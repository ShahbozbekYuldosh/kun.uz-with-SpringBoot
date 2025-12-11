package dasturlash.uz.dto.article;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleFilterDTO {
    private String title;
    private Integer regionId;
    private Integer categoryId;
    private Integer sectionId;
    private LocalDateTime publishedDateFrom;
    private LocalDateTime publishedDateTo;
    private Integer moderatorId;
    private Integer publisherId;
    private String status;
}
