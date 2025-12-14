package dasturlash.uz.repository;

import dasturlash.uz.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer>, JpaSpecificationExecutor<CommentEntity> {

    // parent orqali replies olish
    List<CommentEntity> findAllByParentIdAndVisibleTrueOrderByCreatedDateAsc(Integer parentId);

    List<CommentEntity> findAllByArticle_IdAndVisibleTrueOrderByCreatedDateDesc(Integer articleId);

    Page<CommentEntity> findAllByVisibleTrue(Pageable pageable);

    List<CommentEntity> findByParentId(Integer parentId);

    List<CommentEntity> findByArticleIdAndVisibleTrueOrderByCreatedDateDesc(Integer articleId);
}
