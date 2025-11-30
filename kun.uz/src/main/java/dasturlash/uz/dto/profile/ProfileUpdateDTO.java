package dasturlash.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDTO {
    @NotBlank(message = "Ism bo'sh bo'lmasligi kerak")
    private String name;

    @NotBlank(message = "Username bo'sh bo'lmasligi kerak")
    private String username;

}