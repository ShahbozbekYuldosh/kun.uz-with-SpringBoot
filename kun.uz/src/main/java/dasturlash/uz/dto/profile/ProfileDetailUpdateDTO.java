package dasturlash.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDetailUpdateDTO {

    @NotBlank(message = "Ism majburiy")
    private String name;

    private String surname;
}