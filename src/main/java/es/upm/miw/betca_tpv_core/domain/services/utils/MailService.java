package es.upm.miw.betca_tpv_core.domain.services.utils;

import es.upm.miw.betca_tpv_core.domain.exceptions.MailException;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailSender;
import reactor.core.publisher.Mono;

@Service
public class MailService {

    private final MailSender mailSender;

    public MailService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}@example.com")
    private String from;

    private final String subject = "TPV-Core Info";

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public Mono<Void> send(String to, String msg) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(getFrom());
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(getSubject());
        simpleMailMessage.setText(msg);
        try {
            mailSender.send(simpleMailMessage);
            LogManager.getLogger(this.getClass().getName()).debug(() -> String.format("E-MAIL from: %s, to: %s, subject: %s", from, to, subject));
        } catch (Exception e) {
            throw new MailException(e.getMessage());
        }
        return Mono.empty();
    }
}
