package dasturlash.uz.dto.profile;

import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileCreateDTO {

    @NotBlank(message = "Ism majburiy")
    private String name;

    @NotBlank(message = "Username majburiy")
    private String username;

    @NotBlank(message = "Parol majburiy")
    private String password;

    @NotNull(message = "Status majburiy")
    private ProfileStatus profileStatus; // ProfileStatus enum

    @NotNull(message = "Rol ro'yxati majburiy")
    @Size(min = 1, message = "Kamida bitta rol kiritilishi kerak")
    private List<ProfileRole> roleList; // ProfileRole enum Listi
}