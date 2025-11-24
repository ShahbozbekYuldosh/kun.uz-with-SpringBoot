package dasturlash.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "attach")
public class AttachEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "original_name", nullable = false)
    private String originalName; // Faylning original nomi

    @Column(name = "path", nullable = false)
    private String path; // Fayl tizimdagi yoki serverdagi joylashuvi

    @Column(name = "size", nullable = false)
    private Long size; // Fayl hajmi

    @Column(name = "extension", nullable = false)
    private String extension; // Fayl kengaytmasi (jpg, png, pdf, ...)

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
