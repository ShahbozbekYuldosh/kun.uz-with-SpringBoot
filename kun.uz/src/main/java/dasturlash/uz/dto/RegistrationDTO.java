package dasturlash.uz.dto;

import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistrationDTO {
    @NotBlank(message = "Name required")
    private String name;
    @NotBlank(message = "Username required")
    private String username;
    @NotBlank(message = "password required")
    private String password;
    @NotNull(message = "Status majburiy")
    private ProfileStatus profileStatus;
    @NotNull(message = "Rol ro'yxati majburiy")
    private List<ProfileRole> roleList;
}

