package dasturlash.uz.service;

import dasturlash.uz.dto.MessageDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSendingService {

    @Value("${spring.mail.username}")
    private String fromAccount;

    @Autowired
    private JavaMailSender javaMailSender;

    public String sendMimeMessage(MessageDTO dto) {
        try {
            String html = """
        <!doctype html>
        <html lang="en">
        <head>
          <meta charset="utf-8">
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <title>OTP Email</title>

          <style>
            body {
              font-family: Arial, sans-serif;
              background-color: #f4f4f4;
              padding: 0;
              margin: 0;
            }
            .container-sec {
              background-color: #ffffff;
              border-radius: 8px;
              padding: 20px;
              margin-top: 30px;
              box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
              max-width: 600px;
              margin-left: auto;
              margin-right: auto;
            }
            .otp-code {
              font-size: 24px;
              font-weight: bold;
              background-color: #f8f9fa;
              padding: 15px;
              text-align: center;
              border-radius: 8px;
              border: 1px dashed #007bff;
              color: #007bff;
            }
            .btn-verify {
              display: inline-block;
              padding: 10px 20px;
              color: white !important;
              background-color: #007bff;
              border-radius: 6px;
              text-decoration: none;
              font-weight: bold;
            }
            .footer-text {
              color: #6c757d;
              font-size: 14px;
              text-align: center;
              margin-top: 20px;
            }
            .footer-text a {
              color: #007bff;
              text-decoration: none;
            }
            .otp-lock {
              color: #333;
              font-size: 80px;
            }
            .welcome-section {
              background: #144fa9db;
              padding: 30px;
              border-radius: 4px;
              color: #fff;
              font-size: 20px;
              margin: 20px 0px;
            }
            .welcome-text {
              font-family: monospace;
            }
            .app-name {
              font-size: 30px;
              font-weight: 800;
              margin: 7px 0px;
            }
            .verify-text {
              margin-top: 25px;
              font-size: 25px;
              letter-spacing: 3px;
            }
          </style>
        </head>

        <body>
          <div class="container-sec">
            <div class="text-center">
              <div>
                <h1 class="otp-lock"><center>🔒</center></h1>
              </div>
              <div class="welcome-section">
                <div class="app-name"><center>KUN_UZ</center></div>
                <div class="welcome-text">Ro‘yxatdan o‘tganingiz bilan tabriklaymiz!</div>
                <div class="verify-text">Emailingizni tasdiqlab qo‘ying Akaginam 😢</div>
                <div class="email-icon">
                  <h1>📨_________🚛________📬</h1>
                </div>
              </div>

              <h1>Assalomu alaykum, %s 😎</h1>
              <p>Your One-Time Password (OTP) for verification is:</p>
              <div class="otp-code">%s</div>

              <p class="mt-4">Bu kod 10 daqiqa amal qiladi. \s
                                                                                          Shoshiling, bo‘lmasa zerikib ketib qoladi 😄</p>

              <a href="%s" class="btn-verify">Tasdiqlash — bosib qo‘ying!</a>
            </div>

            <div class="footer-text">
              <p>Agar “Kodni kiritdim lekin o'xshamadi-ku?” desangiz 😳 — yana yuboravering <br> har holda mazgilar uchun tekin 😂.</p>
              <p>Hurmat bilan,<br>Mazgilar jamoasi 👨‍💻👨‍💻</p>
            </div>
          </div>
        </body>
        </html>
        """.formatted(dto.getName(), dto.getOtp(), dto.getVerifyUrl());


            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(fromAccount);
            helper.setTo(dto.getToAccount());
            helper.setSubject(dto.getSubject());
            helper.setText(html, true);

            javaMailSender.send(msg);
            return "Mail sent successfully";

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

