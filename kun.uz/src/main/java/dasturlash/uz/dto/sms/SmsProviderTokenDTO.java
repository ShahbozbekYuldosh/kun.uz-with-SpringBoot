package dasturlash.uz.dto.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SmsProviderTokenDTO {
    private String email;
    private String password;
}
