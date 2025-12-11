package dasturlash.uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "region")
public class RegionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "order_number")
    private Integer orderNumber;

    @NotBlank
    @Column(name = "name_uz")
    private String nameUz;

    @NotBlank
    @Column(name = "name_ru")
    private String nameRu;

    @NotBlank
    @Column(name = "name_en")
    private String nameEn;

    @NotBlank
    @Column(name = "name_kr")
    private String nameKr;

    @Column(name = "region_key", nullable = false, unique = true)
    private String regionKey;

    @Column(name = "visible")
    private Boolean visible = true;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

}