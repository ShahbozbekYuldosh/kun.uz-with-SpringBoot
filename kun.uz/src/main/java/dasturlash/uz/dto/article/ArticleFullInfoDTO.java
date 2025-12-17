package dasturlash.uz.dto.article;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ArticleFullInfoDTO {
    private Integer id;
    private String title;
    private String description;
    private String content;

    private Integer sharedCount;
    private Integer viewCount;
    private Integer likeCount;

    private String imageUrl;

    private LocalDateTime createdDate;
    private LocalDateTime publishedDate;

    private ModeratorInfo moderator;
    private RegionInfo region;
    private List<CategoryInfo> categoryList;
    private List<SectionInfo> sectionList;
    private List<TagInfo> tagList;

    @Getter @Setter
    public static class ModeratorInfo {
        private Integer id;
        private String name;
        private String username;
    }

    @Getter @Setter
    public static class RegionInfo {
        private String key;
        private String name;
    }

    @Getter @Setter
    public static class CategoryInfo {
        private String key;
        private String name;
    }

    @Getter @Setter
    public static class SectionInfo {
        private Integer id;
        private String name;
    }

    @Getter @Setter
    public static class TagInfo {
        private Integer id;
        private String name;
    }
}
