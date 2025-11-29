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
          <title>Email Verification</title>

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
              border: 2px solid black;
            }
            .btn-verify {
              display: inline-block;
              padding: 15px 40px;
              color: white !important;
              background-color: #007bff;
              border-radius: 6px;
              text-decoration: none;
              font-weight: bold;
              font-size: 18px;
              margin: 20px 0px;
            }
            .btn-verify:hover {
              background-color: #0056b3;
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
            .email-lock {
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
              color: white !important;
            }
            .verify-text {
              margin-top: 25px;
              font-size: 25px;
              letter-spacing: 3px;
            }
            .link-box {
              background-color: #f8f9fa;
              padding: 15px;
              border-radius: 8px;
              border: 1px dashed #007bff;
              word-wrap: break-word;
              font-size: 12px;
              color: #6c757d;
              margin: 20px 0px;
            }
          </style>
        </head>

        <body>
          <div class="container-sec">
            <div class="text-center">
              <div>
                <h1 class="email-lock"><center>🔒</center></h1>
              </div>
              <div class="welcome-section">
                <div class="app-name"><center><h1>KUN.UZ</h1></center></div>
                <div class="welcome-text">Ro'yxatdan o'tganingiz bilan tabriklaymiz!</div>
                <div class="verify-text">Emailingizni tasdiqlab qo'ying Akaginam 😢</div>
                <div class="email-icon">
                  <h1>📨_________🚛________📬</h1>
                </div>
              </div>

              <h1>Assalomu alaykum, %s 😎</h1>
              <p>Emailingizni tasdiqlash uchun quyidagi tugmani bosing:</p>

              <a href="%s" class="btn-verify">✅ Emailni Tasdiqlash</a>

              <p style="margin-top: 20px;">Yoki quyidagi linkni brauzerga nusxalang:</p>
              <div class="link-box">%s</div>

              <p class="mt-4" style="color: #dc3545; font-weight: bold;">
                ⏰ Bu link 15 daqiqa amal qiladi.
                Shoshiling, bo'lmasa zerikib ketib qoladi 😄
              </p>

              <p style="color: #6c757d; font-size: 14px;">
                Agar tugma ishlamasa, yuqoridagi linkni to'g'ridan-to'g'ri brauzerga joylashtiring.
              </p>
            </div>

            <div class="footer-text">
              <p>Agar "Link ishlamadi-ku?" desangiz 😳 — support@giybat.uz ga murojaat qiling yoki 
              <br>ilovada "Resend Verification" tugmasini bosing.</p>
              <p style="margin-top: 20px; color: #dc3545;">
                ⚠️ Agar siz bu emailni kutmagan bo'lsangiz, e'tibor bermang va o'chirib tashlang.
              </p>
              <p style="margin-top: 20px;">Hurmat bilan,<br><strong>Mazgilar jamoasi</strong> 👨‍💻👩‍💻</p>
            </div>
          </div>
        </body>
        </html>
        """.formatted(dto.getName(), dto.getVerifyUrl(), dto.getVerifyUrl());

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