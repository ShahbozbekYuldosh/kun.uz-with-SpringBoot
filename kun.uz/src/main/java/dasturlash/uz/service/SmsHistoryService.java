package dasturlash.uz.service;

import dasturlash.uz.entity.SmsHistoryEntity;
import dasturlash.uz.enums.SmsStatus;
import dasturlash.uz.repository.SmsHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsHistoryService {

    private final SmsHistoryRepository smsHistoryRepository;

    public void create(String phone, String message, SmsStatus status, String serviceId) {
        SmsHistoryEntity entity = new SmsHistoryEntity();
        entity.setPhone(phone);
        entity.setMessage(message);
        entity.setStatus(status);
        entity.setServiceId(serviceId);

        smsHistoryRepository.save(entity);
    }
}