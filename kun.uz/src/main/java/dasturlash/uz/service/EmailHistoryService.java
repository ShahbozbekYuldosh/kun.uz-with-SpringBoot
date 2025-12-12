package dasturlash.uz.service;

import dasturlash.uz.dto.EmailHistoryDTO;
import dasturlash.uz.entity.EmailHistoryEntity;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.EmailHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailHistoryService {

    private final EmailHistoryRepository repository;
    private static final int EMAIL_LIMIT_PER_MINUTE = 4;

    // 1. Create Email History
    public void create(String email, String message) {

        // oxirgi 1 daqiqada yuborilgan email soni
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        long count = repository.countEmailsSentLastMinute(email, oneMinuteAgo);

        if (count >= EMAIL_LIMIT_PER_MINUTE) {
            throw new AppBadException("1 daqiqada 4 ta emaildan ko‘p yuborish mumkin emas!");
        }

        EmailHistoryEntity entity = new EmailHistoryEntity();
        entity.setEmail(email);
        entity.setMessage(message);
        repository.save(entity);
    }

    // 2. Get by Email
    public List<EmailHistoryDTO> getByEmail(String email) {
        List<EmailHistoryEntity> list = repository.findAllByEmailOrderByCreatedDateDesc(email);
        return list.stream().map(this::toDTO).toList();
    }

    // 3. Get by Date Range
    public List<EmailHistoryDTO> getByDate(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new AppBadException("DATE_RANGE_INVALID");
        }

        List<EmailHistoryEntity> list =
                repository.findAllByCreatedDateBetweenOrderByCreatedDateDesc(from, to);

        return list.stream().map(this::toDTO).toList();
    }

    // 4. Pagination for ADMIN
    public Page<EmailHistoryDTO> getAdminPage(int page, int size) {
        Page<EmailHistoryEntity> paging = repository.findAll(PageRequest.of(page, size));
        return paging.map(this::toDTO);
    }

    // Mapper
    private EmailHistoryDTO toDTO(EmailHistoryEntity entity) {
        EmailHistoryDTO dto = new EmailHistoryDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setMessage(entity.getMessage());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}
