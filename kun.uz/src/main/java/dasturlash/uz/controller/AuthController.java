package dasturlash.uz.controller;

import dasturlash.uz.dto.LoginDTO;
import dasturlash.uz.dto.LoginResponseDTO;
import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.dto.VerificationDTO;
import dasturlash.uz.dto.profile.ResendSmsDTO;
import dasturlash.uz.dto.sms.SmsVerificationDTO;
import dasturlash.uz.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/registeration")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationDTO registrationDTO) {
        return ResponseEntity.ok().body(authService.register(registrationDTO));
    }

    @GetMapping("/verify/email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/verify/phone")
    public ResponseEntity<String> verifyPhone(@Valid @RequestBody VerificationDTO dto) {
        return ResponseEntity.ok(authService.verifyPhone(dto));
    }

    @PostMapping("/verify/sms")
    public ResponseEntity<String> verifySms(@Valid @RequestBody SmsVerificationDTO dto) {
        return ResponseEntity.ok(authService.verifySms(dto));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String username) {
        return ResponseEntity.ok(authService.resendVerificationEmail(username));
    }

    @PostMapping("/resend-verificationSms")
    public ResponseEntity<String> resendVerificationSms(@RequestBody ResendSmsDTO dto) {
        return ResponseEntity.ok(authService.resendVerificationSms(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
