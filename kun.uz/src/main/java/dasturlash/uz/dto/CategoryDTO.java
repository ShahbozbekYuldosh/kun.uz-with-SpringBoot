package dasturlash.uz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Category ma'lumotlari")
public class CategoryDTO {

    private Integer categoryId;

    @Schema(description = "Tartib raqami", example = "1")
    private Integer orderNumber;

    @Schema(description = "O‘zbekcha nomi", example = "Texnologiya")
    private String nameUz;

    @Schema(description = "Ruscha nomi", example = "Технологии")
    private String nameRu;

    @Schema(description = "Inglizcha nomi", example = "Technology")
    private String nameEn;

    @Schema(description = "Krill nomi", example = "Технология")
    private String nameKr;

    @Schema(description = "Unikal key", example = "tech")
    private String categoryKey;

    private Boolean visible;

    @Schema(description = "Yaratilgan sana")
    private LocalDateTime createdDate;

    private String name;
}
