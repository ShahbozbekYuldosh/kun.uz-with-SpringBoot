package dasturlash.uz.repository;

import dasturlash.uz.dto.article.ArticleFilterDTO;
import dasturlash.uz.entity.ArticleEntity;
import dasturlash.uz.enums.ArticleStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {

    // ====================== FIND LAST N BY SECTION ======================
    List<ArticleEntity> findAllBySections_SectionIdAndStatusAndVisibleTrue(
            Integer sectionId,
            ArticleStatusEnum status,
            Pageable pageable
    );

    // ====================== FIND LAST 12 EXCEPT GIVEN IDS ======================
    List<ArticleEntity> findAllByStatusAndVisibleTrueAndIdNotIn(
            ArticleStatusEnum status,
            List<Integer> excludeIds,
            Pageable pageable
    );

    // ====================== FIND LAST N BY CATEGORY ======================
    List<ArticleEntity> findAllByCategories_CategoryIdAndStatusAndVisibleTrue(
            Integer categoryId,
            ArticleStatusEnum status,
            Pageable pageable
    );

    // ====================== FIND LAST N BY REGION ======================
    List<ArticleEntity> findAllByRegion_IdAndStatusAndVisibleTrue(
            Integer regionId,
            ArticleStatusEnum status,
            Pageable pageable
    );

    // ====================== FIND BY ID AND LANG ======================
    Optional<ArticleEntity> findByIdAndVisibleTrue(Integer id);

    // ====================== FILTER FOR PUBLISHER ======================
    @Query("SELECT a FROM ArticleEntity a " +
            "WHERE a.status = 'PUBLISHED' " +
            "AND (:#{#filter.title} IS NULL OR a.title LIKE %:#{#filter.title}%) " +
            "AND (:#{#filter.regionId} IS NULL OR a.region.id = :#{#filter.regionId}) " +
            "AND (:#{#filter.categoryId} IS NULL OR :#{#filter.categoryId} MEMBER OF a.categories) " +
            "AND (:#{#filter.sectionId} IS NULL OR :#{#filter.sectionId} MEMBER OF a.sections) " +
            "AND (:#{#filter.publishedDateFrom} IS NULL OR a.publishedDate >= :#{#filter.publishedDateFrom}) " +
            "AND (:#{#filter.publishedDateTo} IS NULL OR a.publishedDate <= :#{#filter.publishedDateTo})")
    List<ArticleEntity> filterPublisher(ArticleFilterDTO filter, Pageable pageable);

    // ====================== FILTER FOR MODERATOR ======================
    @Query("SELECT a FROM ArticleEntity a " +
            "WHERE a.moderator.id = :moderatorId " +
            "AND (:#{#filter.title} IS NULL OR a.title LIKE %:#{#filter.title}%) " +
            "AND (:#{#filter.regionId} IS NULL OR a.region.id = :#{#filter.regionId}) " +
            "AND (:#{#filter.categoryId} IS NULL OR :#{#filter.categoryId} MEMBER OF a.categories) " +
            "AND (:#{#filter.sectionId} IS NULL OR :#{#filter.sectionId} MEMBER OF a.sections) " +
            "AND (:#{#filter.createdDateFrom} IS NULL OR a.createdDate >= :#{#filter.createdDateFrom}) " +
            "AND (:#{#filter.createdDateTo} IS NULL OR a.createdDate <= :#{#filter.createdDateTo})")
    List<ArticleEntity> filterModerator(ArticleFilterDTO filter, Integer moderatorId, Pageable pageable);

    // ====================== FILTER FOR ADMIN ======================
    @Query("SELECT a FROM ArticleEntity a " +
            "WHERE (:#{#filter.title} IS NULL OR a.title LIKE %:#{#filter.title}%) " +
            "AND (:#{#filter.regionId} IS NULL OR a.region.id = :#{#filter.regionId}) " +
            "AND (:#{#filter.categoryId} IS NULL OR :#{#filter.categoryId} MEMBER OF a.categories) " +
            "AND (:#{#filter.sectionId} IS NULL OR :#{#filter.sectionId} MEMBER OF a.sections) " +
            "AND (:#{#filter.createdDateFrom} IS NULL OR a.createdDate >= :#{#filter.createdDateFrom}) " +
            "AND (:#{#filter.createdDateTo} IS NULL OR a.createdDate <= :#{#filter.createdDateTo}) " +
            "AND (:#{#filter.publishedDateFrom} IS NULL OR a.publishedDate >= :#{#filter.publishedDateFrom}) " +
            "AND (:#{#filter.publishedDateTo} IS NULL OR a.publishedDate <= :#{#filter.publishedDateTo}) " +
            "AND (:#{#filter.moderatorId} IS NULL OR a.moderator.id = :#{#filter.moderatorId}) " +
            "AND (:#{#filter.publisherId} IS NULL OR a.publisher.id = :#{#filter.publisherId}) " +
            "AND (:#{#filter.status} IS NULL OR a.status = :#{#filter.status})")
    List<ArticleEntity> filterAdmin(ArticleFilterDTO filter, Pageable pageable);
}

