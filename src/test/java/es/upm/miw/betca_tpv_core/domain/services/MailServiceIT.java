package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.services.utils.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import reactor.test.StepVerifier;


@TestConfig
public class MailServiceIT {

    @Autowired
    private MailService mailService;

    @Test
    @DisabledIf(
            expression = "#{environment['spring.mail.smtp_data'] == 'is_not_present'}",
            loadContext = true,
            reason = "Disabled if SMTP data is not present"
    )
    public void testSendMessage() {
        StepVerifier
                .create(mailService.send("test@example.com", "Test body message"))
                .expectComplete()
                .verify();
    }

}
