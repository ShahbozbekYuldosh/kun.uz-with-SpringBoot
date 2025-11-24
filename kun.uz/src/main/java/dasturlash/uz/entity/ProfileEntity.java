package dasturlash.uz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Profile")
public class ProfileEntity {
    @Id
    private String id;
}
