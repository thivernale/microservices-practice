package org.thivernale.notificationservice.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private SpringTemplateEngine templateEngine;
    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromAddress", "from@example.com");
    }

    @Test
    void whenSendPaymentSuccessEmailToEmptyEmail_thenEmailNotSent() {
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);

        assertThatThrownBy(
            () -> emailService.sendPaymentSuccessEmail("", " ", "", BigDecimal.valueOf(1.0))
        )
            .isInstanceOf(MessagingException.class)
            .hasMessage("Illegal address");

        verify(mailSender, never()).send(message);
    }

    @Test
    void whenSendPaymentSuccessEmailToValidEmail_thenEmailSent() {
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("mocked text");

        assertThatNoException().isThrownBy(() ->
            emailService.sendPaymentSuccessEmail("test@example.com", " ", "", BigDecimal.valueOf(1.0))
        );

        verify(mailSender, times(1)).send(message);
    }
}
