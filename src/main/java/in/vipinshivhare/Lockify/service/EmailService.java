package in.vipinshivhare.Lockify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Async
    public void sendWelcomeEmail(String toEmail, String name){
        try {
            log.info("Attempting to send welcome email to: {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Our Platform");
            message.setText("Hello "+name+",\n\nThanks for registering with us!\n\nRegards, \nLockify Team");
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
        }
    }

    @Async
    public void sendResetOtpEmail(String toEmail, String otp){
        try {
            log.info("Attempting to send reset OTP email to: {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP");
            message.setText("Your otp for resetting your password is "+otp+". Use this OTP to proceed with resetting your password.");
            mailSender.send(message);
            log.info("Reset OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send reset OTP email to {}: {}", toEmail, e.getMessage(), e);
        }
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp){
        try {
            log.info("Attempting to send verification OTP email to: {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Account Verification OTP");
            message.setText("Your OTP is "+otp+". Verify your account using this OTP");
            mailSender.send(message);
            log.info("Verification OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification OTP email to {}: {}", toEmail, e.getMessage(), e);
        }
    }

}
