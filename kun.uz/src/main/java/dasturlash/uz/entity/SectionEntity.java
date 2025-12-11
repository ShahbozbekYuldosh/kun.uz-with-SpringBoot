package dasturlash.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "section")
public class SectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "name_uz", nullable = false)
    private String nameUz;

    @Column(name = "name_ru", nullable = false)
    private String nameRu;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_kr", nullable = false)
    private String nameKr;

    @Column(name = "section_key", unique = true, nullable = false)
    private String sectionKey;

    @Column(name = "visible")
    private Boolean visible = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "image_id")
    private Integer imageId;
}
