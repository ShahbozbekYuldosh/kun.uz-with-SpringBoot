package dasturlash.uz.repository;

import dasturlash.uz.entity.SmsHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SmsHistoryRepository extends JpaRepository<SmsHistoryEntity, Integer> {

    Integer countByPhoneAndCreatedDateAfter(String phone, LocalDateTime after);

    List<SmsHistoryEntity> findAllByPhoneOrderByCreatedDateDesc(String phone);

    Page<SmsHistoryEntity> findAllByCreatedDateBetweenOrderByCreatedDateDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<SmsHistoryEntity> findAll(Pageable pageable);
}
