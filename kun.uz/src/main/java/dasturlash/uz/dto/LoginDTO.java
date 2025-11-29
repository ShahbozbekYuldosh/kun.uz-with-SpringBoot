package dasturlash.uz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "Username bo'sh bo'lishi mumkin emas")
    private String username;

    @NotBlank(message = "Parol bo'sh bo'lishi mumkin emas")
    private String password;

}