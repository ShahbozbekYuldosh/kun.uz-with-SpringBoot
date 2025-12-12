package dasturlash.uz.repository;

import dasturlash.uz.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<TagEntity, Integer> {
    Optional<TagEntity> findByName(String name);
    boolean existsByName(String name);
}
