package dasturlash.uz.service;

import dasturlash.uz.dto.sms.SmsProviderTokenDTO;
import dasturlash.uz.dto.sms.SmsTokenProviderResponse;
import dasturlash.uz.exps.AppBadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsSendingService {

    private final RestTemplate restTemplate;

    @Value("${sms.provider.url.token}")
    private String tokenUrl;
    @Value("${sms.provider.url.send}")
    private String sendSmsUrl;
    @Value("${sms.provider.login}")
    private String login;
    @Value("${sms.provider.password}")
    private String password;
    @Value("${sms.provider.senderName}")
    private String senderName;

    private String currentToken;
    private long tokenExpiryTime = 0;

    private String getAuthToken() {
        if (currentToken != null && tokenExpiryTime > System.currentTimeMillis()) {
            return currentToken;
        }

        SmsProviderTokenDTO tokenDto = new SmsProviderTokenDTO(login, password);

        try {
            ResponseEntity<SmsTokenProviderResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    tokenDto,
                    SmsTokenProviderResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                SmsTokenProviderResponse body = response.getBody();
                currentToken = body.getToken();
                tokenExpiryTime = System.currentTimeMillis() + (body.getExpiresIn() * 1000L);
                return currentToken;
            }
            throw new AppBadException("SMS Provider token olib bo'lmadi: Status " + response.getStatusCode());

        } catch (Exception e) {
            throw new AppBadException("SMS Provider bilan ulanishda xato: " + e.getMessage());
        }
    }

    public void sendSms(String phone, String message) {
     /**   String token = getAuthToken(); // 1. Tokenni olamiz

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token); // Olingan tokenni headerga qo'shamiz

        // 2. Xabar yuborish uchun Body (provayderning formatiga qarab)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("mobile_phone", phone);
        requestBody.put("message", message);
        requestBody.put("from", senderName);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 3. SMS yuborish API'sini chaqirish
            ResponseEntity<String> response = restTemplate.exchange(
                    sendSmsUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                // Agar provayder xato statusi qaytarsa
                throw new AppBadException("SMS yuborishda xatolik: " + response.getBody());
            }
        } catch (Exception e) {
            // Tarmoq yoki ulanish xatosi
            throw new AppBadException("SMS yuborish so'rovi muvaffaqiyatsiz tugadi: " + e.getMessage());
        }  **/


        log.warn("--- SMS TEST REJIMIDA YUBORILDI ---");
        log.warn("Telefon: {}", phone);
        log.warn("Xabar: {}", message);
        log.warn("---------------------------------");


    }
}