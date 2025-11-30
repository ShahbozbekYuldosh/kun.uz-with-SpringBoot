package dasturlash.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateDTO {

    @NotBlank(message = "Joriy parol majburiy")
    private String oldPassword;

    @NotBlank(message = "Yangi parol majburiy")
    private String newPassword;
}