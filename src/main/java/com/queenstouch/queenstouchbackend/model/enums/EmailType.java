package com.queenstouch.queenstouchbackend.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailType {

    EMAIL_VERIFICATION(
            "Verify your Queenstouch account",
            "email/verification"
    ),
    PASSWORD_RESET(
            "Reset your Queenstouch password",
            "email/password-reset"
    );

    private final String subject;
    private final String template;
}
