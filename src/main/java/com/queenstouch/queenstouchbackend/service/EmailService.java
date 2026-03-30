package com.queenstouch.queenstouchbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.queenstouch.queenstouchbackend.config.AppProperties;
import com.queenstouch.queenstouchbackend.model.EmailLog;
import com.queenstouch.queenstouchbackend.model.enums.EmailStatus;
import com.queenstouch.queenstouchbackend.repository.EmailLogRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final String FROM_ADDRESS = "noreply@queenstouch.com"; // Default from

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final EmailLogRepository emailLogRepository;

    @Async
    public void sendVerificationEmail(String to, String token, String firstName) {
        String verificationUrl = appProperties.getFrontendUrl()
                + "/verify-email?token=" + token + "&email=" + to;
        Context ctx = new Context();
        ctx.setVariable("verificationUrl", verificationUrl);
        ctx.setVariable("token", token);
        ctx.setVariable("firstName", firstName);

        sendHtmlEmail(to, "Verify your Queenstouch account", "email/verification", ctx);
    }

    @Async
    public void sendPasswordResetEmail(String to, String token, String firstName) {
        Context ctx = new Context();
        ctx.setVariable("token", token);
        ctx.setVariable("firstName", firstName);

        sendHtmlEmail(to, "Reset your Queenstouch password", "email/password-reset", ctx);
    }

    private void sendHtmlEmail(String to, String subject, String template, Context ctx) {
        String html = templateEngine.process(template, ctx);

        EmailLog emailLog = createEmailLog(to, subject, template, html);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);

            markEmailLogSuccessful(emailLog);
            log.debug("Email '{}' sent to {} [logId={}]", subject, to, emailLog.getId());

        } catch (MessagingException e) {
            markEmailLogFailed(e, emailLog);
            log.error("Failed to send email '{}' to {} [logId={}]: {}",
                    subject, to, emailLog.getId(), e.getMessage());
        } catch (Exception e) {
             markEmailLogFailed(e, emailLog);
             log.error("Failed to process email '{}' to {} [logId={}]: {}",
                    subject, to, emailLog.getId(), e.getMessage());
        }
    }

    private EmailLog createEmailLog(String to, String subject, String template, String html) {
        return emailLogRepository.save(
                EmailLog.builder()
                        .fromAddress(FROM_ADDRESS)
                        .toAddress(to)
                        .subject(subject)
                        .template(template)
                        .body(html)
                        .status(EmailStatus.PENDING)
                        .createdAt(Instant.now())
                        .build()
        );
    }

    private void markEmailLogSuccessful(EmailLog emailLog) {
        emailLog.setStatus(EmailStatus.SUCCESS);
        emailLog.setSentAt(Instant.now());
        emailLogRepository.save(emailLog);
    }

    private void markEmailLogFailed(Exception e, EmailLog emailLog) {
        emailLog.setStatus(EmailStatus.FAILED);
        emailLog.setErrorMessage(e.getMessage());
        emailLogRepository.save(emailLog);
    }
}
