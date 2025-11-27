package dasturlash.uz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class emailSendingService {
    @Value("${spring.mail.username}")
    private String fromAccount;

    @Autowired
    private JavaMailSender mailSender;

    private void sendEmail(String email, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAccount);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendRegistrationEmail(String email, Integer profileId) {
        String subject = "Registration Confirmation";
        String body = "Registration Confirmation. Please to link your account = " + profileId;
        sendEmail(email, subject, body);
    }
}

