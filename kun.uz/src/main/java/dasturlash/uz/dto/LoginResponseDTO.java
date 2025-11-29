package dasturlash.uz.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginResponseDTO {
    private String name;
    private String username;
    private String role; // ProfileRole enumining String ko'rinishi
    private String token; // JWT Access Token

}