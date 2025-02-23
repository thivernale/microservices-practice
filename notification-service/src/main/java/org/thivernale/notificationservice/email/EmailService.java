package org.thivernale.notificationservice.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thivernale.notificationservice.notification.event.domain.Product;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_RELATED;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${application.email.from:}")
    private String fromAddress;

    @Async
    public void sendPaymentSuccessEmail(
        String customerEmail,
        String customerName,
        String orderReference,
        BigDecimal amount
    ) throws MessagingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderReference", orderReference);
        variables.put("amount", amount);
        variables.put("customerName", customerName);

        sendEmail(customerEmail, EmailTemplate.PAYMENT_CONFIRMATION, variables);
    }

    @Async
    public void sendOrderConfirmationEmail(
        String customerEmail,
        String customerName,
        String orderReference,
        BigDecimal amount,
        List<Product> products
    ) throws MessagingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderReference", orderReference);
        variables.put("totalAmount", amount);
        variables.put("customerName", customerName);
        variables.put("products", products);

        sendEmail(customerEmail, EmailTemplate.ORDER_CONFIRMATION, variables);
    }

    private void sendEmail(
        String toAddress,
        EmailTemplate emailTemplate,
        Map<String, Object> variables
    ) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_RELATED, UTF_8.name());

        helper.setFrom(fromAddress);
        helper.setSubject(emailTemplate.getSubject());

        try {
            helper.setTo(toAddress);
            helper.setText(templateEngine.process(emailTemplate.getTemplate(), new Context(Locale.getDefault(),
                variables)), true);

            mailSender.send(mimeMessage);

            log.info("Email sent to {} with template {}", toAddress, emailTemplate.getTemplate());
        } catch (MessagingException | MailException e) {
            log.warn("Cannot send email to %s with template %s".formatted(toAddress, emailTemplate.getTemplate()), e);
            throw e;
        }
    }
}
