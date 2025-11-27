package dasturlash.uz.service;

import dasturlash.uz.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsEmailService {

/*    private final EmailHistoryService emailHistoryService;
    private final SmsHistoryService smsHistoryService;

    public void sendVerificationCode(ProfileEntity profile) {
        String code = String.valueOf((int)(Math.random() * 90000) + 10000);

        // emailga yuborish
        if (profile.getUsername().contains("@")) {
            emailHistoryService.sendEmail(profile.getUsername(), code);
        } else {
            smsHistoryService.sendSms(profile.getUsername(), code);
        }
    }*/
}

