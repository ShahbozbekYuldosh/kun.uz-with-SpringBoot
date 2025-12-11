package dasturlash.uz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Section ma'lumotlari")
public class SectionDTO {

    private Integer sectionId;

    @Schema(description = "Tartib raqami", example = "1")
    private Integer orderNumber;

    @Schema(description = "O‘zbekcha nomi", example = "Yangiliklar")
    private String nameUz;

    @Schema(description = "Ruscha nomi", example = "Новости")
    private String nameRu;

    @Schema(description = "Inglizcha nomi", example = "News")
    private String nameEn;

    @Schema(description = "Krillcha nomi", example = "News")
    private String nameKr;

    @Schema(description = "Unikal key", example = "news")
    private String sectionKey;

    @Schema(description = "Ko‘rinishi", example = "true")
    private Boolean visible;

    @Schema(description = "Yaratilgan sana")
    private LocalDateTime createdDate;

    @Schema(description = "Hozirgi til bo‘yicha nomi")
    private String name;
}
