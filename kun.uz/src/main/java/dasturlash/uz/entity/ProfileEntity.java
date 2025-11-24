package dasturlash.uz.entity;

import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profile")
@Getter
@Setter
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(unique = true)
    private String username; // email yoki phone

    private String password;

    @Enumerated(EnumType.STRING)
    private ProfileStatus status = ProfileStatus.ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_role", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<ProfileRole> roleList = new HashSet<>();

    @Column(name = "photo_id")
    private String photoId;

    private Boolean visible;
    private LocalDateTime createdDate = LocalDateTime.now();
}
