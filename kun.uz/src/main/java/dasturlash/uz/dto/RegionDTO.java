package dasturlash.uz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegionDTO {
    private Integer id;

    private Integer orderNumber;
    private String nameUz;
    private String nameRu;
    private String nameEn;
    private String nameKr;
    private String regionKey;


    private LocalDateTime createdDate;
    private String name;
}