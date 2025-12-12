package dasturlash.uz.repository;

import dasturlash.uz.entity.EmailHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailHistoryRepository extends JpaRepository<EmailHistoryEntity, Integer> {

    List<EmailHistoryEntity> findAllByEmailOrderByCreatedDateDesc(String email);

    List<EmailHistoryEntity> findAllByCreatedDateBetweenOrderByCreatedDateDesc(LocalDateTime from, LocalDateTime to);

    Page<EmailHistoryEntity> findAll(Pageable pageable);

    @Query("SELECT COUNT(e) FROM EmailHistoryEntity e " +
            "WHERE e.email = :email AND e.createdDate >= :fromTime")
    long countEmailsSentLastMinute(String email, LocalDateTime fromTime);
}
