package dasturlash.uz.dto.article;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ArticleCreateDTO {
    private String title;
    private String description;
    private String content;
    private Integer imageId;
    private Integer regionId;
    private List<Integer> categoryList;
    private List<Integer> sectionList;
}