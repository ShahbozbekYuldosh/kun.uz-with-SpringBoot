package dasturlash.uz.dto.article;

import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.dto.SectionDTO;
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
    private String imageId;
    private String imageUrl;
    private RegionInfo region;
    private List<CategoryInfo> categoryList;
    private List<SectionInfo> sectionList;
    private List<String> tagList;
    private LocalDateTime publishedDate;


    @Getter
    @Setter
    public static class RegionInfo {
        private String key;
        private String name;
    }

    @Getter
    @Setter
    public static class CategoryInfo {
        private String key;
        private String name;
    }

    @Getter
    @Setter
    public static class SectionInfo {
        private Integer id;
        private String name;
    }
}
