package dasturlash.uz.dto;

import dasturlash.uz.enums.ProfileRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegistrationDTO {
    @NotBlank(message = "Name required")
    private String name;
    @NotBlank(message = "Username required")
    private String username;
    @NotBlank(message = "password required")
    private String password;
    private ProfileRole profileRole;

}

