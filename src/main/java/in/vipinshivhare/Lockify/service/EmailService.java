package in.vipinshivhare.Lockify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from:}")
    private String fromEmail;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${app.email.senderName:Lockify}")
    private String senderName;

    private String resolveFromAddress() {
        if (fromEmail != null && !fromEmail.isBlank()) {
            return fromEmail;
        }
        if (mailUsername != null && !mailUsername.isBlank()) {
            return mailUsername;
        }
        return null;
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String name){
        try {
            log.info("Attempting to send welcome email to: {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            String from = resolveFromAddress();
            if (from != null) {
                message.setFrom(from);
                message.setReplyTo(from);
            }
            message.setTo(toEmail);
            message.setSubject("Welcome to Our Platform");
            message.setText("Hello "+name+",\n\nThanks for registering with us!\n\nRegards, \nLockify Team");
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
            tryApiFallback(toEmail, "Welcome to Our Platform", "Hello "+name+",\n\nThanks for registering with us!\n\nRegards, \nLockify Team");
        }
    }

    @Async
    public void sendResetOtpEmail(String toEmail, String otp){
        try {
            log.info("Attempting to send reset OTP email to: {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            String from = resolveFromAddress();
            if (from != null) {
                message.setFrom(from);
                message.setReplyTo(from);
            }
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP");
            message.setText("Your otp for resetting your password is "+otp+". Use this OTP to proceed with resetting your password.");
            mailSender.send(message);
            log.info("Reset OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send reset OTP email to {}: {}", toEmail, e.getMessage(), e);
            tryApiFallback(toEmail, "Password Reset OTP", "Your otp for resetting your password is "+otp+". Use this OTP to proceed with resetting your password.");
        }
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp){
        try {
            log.info("Attempting to send verification OTP email to: {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            String from = resolveFromAddress();
            if (from != null) {
                message.setFrom(from);
                message.setReplyTo(from);
            }
            message.setTo(toEmail);
            message.setSubject("Account Verification OTP");
            message.setText("Your OTP is "+otp+". Verify your account using this OTP");
            mailSender.send(message);
            log.info("Verification OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification OTP email to {}: {}", toEmail, e.getMessage(), e);
            tryApiFallback(toEmail, "Account Verification OTP", "Your OTP is "+otp+". Verify your account using this OTP");
        }
    }

    private void tryApiFallback(String toEmail, String subject, String text) {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            log.warn("Brevo API key not configured; skipping API fallback for email to {}", toEmail);
            return;
        }
        try {
            boolean ok = sendViaBrevoApi(toEmail, subject, text);
            if (ok) {
                log.info("Email sent via Brevo API to {}", toEmail);
            } else {
                log.error("Brevo API fallback failed for {}", toEmail);
            }
        } catch (Exception ex) {
            log.error("Brevo API fallback threw error for {}: {}", toEmail, ex.getMessage(), ex);
        }
    }

    private boolean sendViaBrevoApi(String toEmail, String subject, String text) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> payload = new HashMap<>();
        Map<String, String> sender = new HashMap<>();
        String from = resolveFromAddress();
        sender.put("email", from != null ? from : (mailUsername != null ? mailUsername : "noreply@lockify.local"));
        sender.put("name", senderName);

        Map<String, String> to = new HashMap<>();
        to.put("email", toEmail);

        payload.put("sender", sender);
        payload.put("to", List.of(to));
        payload.put("subject", subject);
        payload.put("textContent", text);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        String url = "https://api.brevo.com/v3/smtp/email";
        Map response = restTemplate.postForObject(url, entity, Map.class);
        return response != null && response.containsKey("messageId");
    }

}
