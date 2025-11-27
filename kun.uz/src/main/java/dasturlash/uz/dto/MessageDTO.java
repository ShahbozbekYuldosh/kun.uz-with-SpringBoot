package dasturlash.uz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
    private String toAccount;
    private String subject;
    private String name;
    private String otp;
    private String verifyUrl;
}