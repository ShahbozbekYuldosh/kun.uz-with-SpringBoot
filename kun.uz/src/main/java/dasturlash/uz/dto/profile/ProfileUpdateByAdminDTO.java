package dasturlash.uz.dto.profile;

import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileUpdateByAdminDTO {

    @NotBlank(message = "Ism majburiy")
    private String name;

    @NotBlank(message = "Username majburiy")
    private String username;

    @NotNull(message = "Status majburiy")
    private ProfileStatus profileStatus;

    @NotNull(message = "Rol ro'yxati majburiy")
    private List<ProfileRole> roleList;
}