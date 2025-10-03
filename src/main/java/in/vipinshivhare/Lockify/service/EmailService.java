package in.vipinshivhare.Lockify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${MAIL_FROM:}")
    private String fromEmail;

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${app.email.senderName:Lockify}")
    private String senderName;

    private String resolveFromAddress() {
        return (fromEmail != null && !fromEmail.isBlank()) ? fromEmail : "noreply@lockify.local";
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String name){
        log.info("Attempting to send welcome email to: {}", toEmail);
        sendViaBrevoApi(toEmail, "Welcome to Our Platform", "Hello "+name+",\n\nThanks for registering with us!\n\nRegards, \nLockify Team");
    }

    @Async
    public void sendResetOtpEmail(String toEmail, String otp){
        log.info("Attempting to send reset OTP email to: {}", toEmail);
        sendViaBrevoApi(toEmail, "Password Reset OTP", "Your otp for resetting your password is "+otp+". Use this OTP to proceed with resetting your password.");
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp){
        log.info("Attempting to send verification OTP email to: {}", toEmail);
        sendViaBrevoApi(toEmail, "Account Verification OTP", "Your OTP is "+otp+". Verify your account using this OTP");
    }

    private void sendViaBrevoApi(String toEmail, String subject, String text) {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            log.error("Brevo API key not configured; cannot send email to {}", toEmail);
            return;
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> payload = new HashMap<>();
            Map<String, String> sender = new HashMap<>();
            sender.put("email", resolveFromAddress());
            sender.put("name", senderName);

            Map<String, String> to = new HashMap<>();
            to.put("email", toEmail);

            payload.put("sender", sender);
            payload.put("to", List.of(to));
            payload.put("subject", subject);
            payload.put("textContent", text);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            String url = "https://api.brevo.com/v3/smtp/email";
            Map<?, ?> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.containsKey("messageId")) {
                log.info("Email sent via Brevo API to {}", toEmail);
            } else {
                log.error("Brevo API did not return messageId for {}. Response: {}", toEmail, response);
            }
        } catch (Exception ex) {
            log.error("Brevo API error for {}: {}", toEmail, ex.getMessage(), ex);
        }
    }

}
