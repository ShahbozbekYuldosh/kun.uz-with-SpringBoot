package dasturlash.uz.repository;

import dasturlash.uz.entity.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, Integer> {

    Optional<SectionEntity> findBySectionKey(String key);

    List<SectionEntity> findAllByVisibleIsTrueOrderByOrderNumber();

    @Modifying
    @Query("UPDATE SectionEntity s SET s.visible = :visible WHERE s.sectionId = :id")
    int updateVisibleById(Integer id, Boolean visible);
}
