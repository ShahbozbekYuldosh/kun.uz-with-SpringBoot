package dasturlash.uz.dto.sms;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SmsHistoryDTO {
    private Integer id;
    private String phone;
    private String message;
    private String status;
    private String serviceId;
    private LocalDateTime createdDate;
}