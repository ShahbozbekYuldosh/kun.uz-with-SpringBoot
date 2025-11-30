package dasturlash.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoUpdateDTO {

    @NotBlank(message = "Attach ID majburiy")
    private String attachId;
}