package dasturlash.uz.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AttachDTO {

    private String id;
    private String originName;
    private Long size;
    private String extension;
    private String url;
    private LocalDateTime createdData;
}