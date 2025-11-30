package dasturlash.uz.dto.sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsVerificationDTO {
    private String username;
    private String verificationCode;
}
