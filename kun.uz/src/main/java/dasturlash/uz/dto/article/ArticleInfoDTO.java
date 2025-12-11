package dasturlash.uz.dto.article;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ArticleInfoDTO {
    private Integer id;
    private String title;
    private String description;
    private String content;
    private Integer sharedCount;
    private Integer viewCount;
    private LocalDateTime publishedDate;
    private String regionName;
    private String regionKey;
    private List<String> categoryNames;
    private List<String> sectionNames;
    private Integer imageId;
    private String imageUrl;
}
