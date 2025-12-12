package dasturlash.uz.dto.article;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ArticleUpdateDTO {
    private String title;
    private String description;
    private String content;
    private String imageId;
    private Integer regionId;
    private List<Integer> categoryList;
    private List<Integer> sectionList;
}