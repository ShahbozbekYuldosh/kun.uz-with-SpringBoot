package dasturlash.uz.dto.sms;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsTokenProviderResponse {
    private String message;
    private Data data;
    private String token_type;
    private Long expires_in;

    public long getExpiresIn() {
        return expires_in != null ? expires_in : 0;
    }

    public String getToken() {
        return data != null ? data.getToken() : null;
    }

    @Getter
    @Setter
    public static class Data {
        private String token;
    }
}