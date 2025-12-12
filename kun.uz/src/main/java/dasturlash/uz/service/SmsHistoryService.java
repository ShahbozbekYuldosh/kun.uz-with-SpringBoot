package dasturlash.uz.service;

import dasturlash.uz.dto.sms.SmsHistoryDTO;
import dasturlash.uz.entity.SmsHistoryEntity;
import dasturlash.uz.enums.SmsStatus;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.SmsHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmsHistoryService {

    private final SmsHistoryRepository smsHistoryRepository;

    public void create(String phone, String message, SmsStatus status, String serviceId) {
        // Rate limit tekshiruvi: 1 daqiqa ichida 4 ta ruxsat
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        long count = smsHistoryRepository.countByPhoneAndCreatedDateAfter(phone, oneMinuteAgo);
        if (count >= 4) {
            throw new AppBadException("Too many SMS requests. Please try again later.");
        }

        SmsHistoryEntity entity = new SmsHistoryEntity();
        entity.setPhone(phone);
        entity.setMessage(message);
        entity.setStatus(status);
        entity.setServiceId(serviceId);

        smsHistoryRepository.save(entity);
    }

    /** Telefon bo'yicha sms history list (oxirgi birinchi) */
    public List<SmsHistoryDTO> getByPhone(String phone) {
        List<SmsHistoryEntity> list = smsHistoryRepository.findAllByPhoneOrderByCreatedDateDesc(phone);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Berilgan sana bo'yicha (from..to) paginated natija */
    public Page<SmsHistoryDTO> getByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<SmsHistoryEntity> result = smsHistoryRepository.findAllByCreatedDateBetweenOrderByCreatedDateDesc(from, to, pageable);
        return result.map(this::toDto);
    }

    /** Admin uchun umumiy pagination */
    public Page<SmsHistoryDTO> paginationForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<SmsHistoryEntity> result = smsHistoryRepository.findAll(pageable);
        return result.map(this::toDto);
    }

    private SmsHistoryDTO toDto(SmsHistoryEntity e) {
        SmsHistoryDTO dto = new SmsHistoryDTO();
        dto.setId(e.getId());
        dto.setPhone(e.getPhone());
        dto.setMessage(e.getMessage());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setServiceId(e.getServiceId());
        dto.setCreatedDate(e.getCreatedDate());
        return dto;
    }
}
