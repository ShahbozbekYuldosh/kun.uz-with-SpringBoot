package dasturlash.uz.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmailHistoryDTO {
    private Integer id;
    private String email;
    private String message;
    private LocalDateTime createdDate;
}
