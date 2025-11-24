package dasturlash.uz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String username; // email yoki telefon
    @NotBlank
    private String password;
}

