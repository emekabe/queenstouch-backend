package com.queenstouch.queenstouchbackend.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import com.queenstouch.queenstouchbackend.config.AppProperties;
import com.queenstouch.queenstouchbackend.model.enums.EmailType;
import com.queenstouch.queenstouchbackend.model.EmailLog;
import com.queenstouch.queenstouchbackend.model.enums.EmailStatus;
import com.queenstouch.queenstouchbackend.repository.EmailLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Instant;

@Service
@Slf4j
public class EmailService {

    private final Resend resendClient;
    private final SpringTemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final EmailLogRepository emailLogRepository;

    public EmailService(AppProperties appProperties,
                        SpringTemplateEngine templateEngine,
                        EmailLogRepository emailLogRepository) {
        this.appProperties = appProperties;
        this.templateEngine = templateEngine;
        this.emailLogRepository = emailLogRepository;
        this.resendClient = new Resend(appProperties.getResend().getApiKey());
    }

    @Async
    public void sendVerificationEmail(String to, String token, String firstName) {
        String verificationUrl = appProperties.getFrontendUrl()
                + "/auth/verify?email=" + to + "&otp=" + token;

        Context ctx = new Context();
        ctx.setVariable("verificationUrl", verificationUrl);
        ctx.setVariable("token", token);
        ctx.setVariable("firstName", firstName);

        sendHtmlEmail(to, EmailType.EMAIL_VERIFICATION, ctx);
    }

    @Async
    public void sendPasswordResetEmail(String to, String token, String firstName) {
        Context ctx = new Context();
        ctx.setVariable("token", token);
        ctx.setVariable("firstName", firstName);

        sendHtmlEmail(to, EmailType.PASSWORD_RESET, ctx);
    }

    private void sendHtmlEmail(String to, EmailType emailType, Context ctx) {
        String subject = emailType.getSubject();
        String template = emailType.getTemplate();
        String html = templateEngine.process(template, ctx);

        AppProperties.Resend resendCfg = appProperties.getResend();
        String from = resendCfg.getFromName() + " <" + resendCfg.getFromAddress() + ">";

        EmailLog emailLog = createEmailLog(to, subject, template, emailType, html);

        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            CreateEmailResponse response = resendClient.emails().send(options);

            markEmailLogSuccessful(emailLog);
            log.info("Email '{}' sent to {} via Resend [id={}]", subject, to, response.getId());

        } catch (ResendException e) {
            markEmailLogFailed(e, emailLog);
            log.error("Resend failed for '{}' to {} [logId={}]: {}", subject, to, emailLog.getId(), e.getMessage());
        } catch (Exception e) {
            markEmailLogFailed(e, emailLog);
            log.error("Unexpected error sending '{}' to {} [logId={}]: {}", subject, to, emailLog.getId(), e.getMessage());
        }
    }

    private EmailLog createEmailLog(String to, String subject, String template, EmailType emailType, String html) {
        return emailLogRepository.save(
                EmailLog.builder()
                        .fromAddress(appProperties.getResend().getFromAddress())
                        .toAddress(to)
                        .subject(subject)
                        .template(template)
                        .emailType(emailType.name())
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
