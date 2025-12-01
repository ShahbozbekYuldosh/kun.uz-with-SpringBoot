package dasturlash.uz.repository;

import dasturlash.uz.entity.SmsHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsHistoryRepository extends JpaRepository<SmsHistoryEntity, Integer> {

}