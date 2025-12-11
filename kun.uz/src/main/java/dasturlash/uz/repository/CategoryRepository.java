package dasturlash.uz.repository;

import dasturlash.uz.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    Optional<CategoryEntity> findByCategoryKey(String key);

    List<CategoryEntity> findAllByVisibleIsTrueOrderByOrderNumber();

    @Modifying
    @Query("UPDATE CategoryEntity c SET c.visible = :visible WHERE c.categoryId = :id")
    int updateVisibleById(Integer id, Boolean visible);
}
