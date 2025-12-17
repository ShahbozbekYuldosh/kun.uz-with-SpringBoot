package dasturlash.uz.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleEditDTO {

    @NotBlank(message = "Title required")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;

    @NotBlank(message = "Description required")
    @Size(max = 500, message = "Description max 500 characters")
    private String description;

    @NotBlank(message = "Content required")
    private String content;

    @NotNull(message = "Region required")
    private Integer regionId;

    @NotNull(message = "Category list required")
    private List<Integer> categoryIdList;

    @NotNull(message = "Section list required")
    private List<Integer> sectionIdList;

    private Integer imageId;

    private List<String> tagList;
}
