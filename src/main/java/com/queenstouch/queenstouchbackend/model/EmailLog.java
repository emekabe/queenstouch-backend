package com.queenstouch.queenstouchbackend.model;

import com.queenstouch.queenstouchbackend.model.enums.EmailStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "email_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {

    @Id
    private String id;

    private String fromAddress;

    @Indexed
    private String toAddress;

    private String subject;

    private String template;

    private String emailType;

    private String body;

    @Builder.Default
    private EmailStatus status = EmailStatus.PENDING;

    private String errorMessage;

    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant sentAt;
}
