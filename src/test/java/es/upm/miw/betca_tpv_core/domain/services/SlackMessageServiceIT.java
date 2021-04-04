package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import es.upm.miw.betca_tpv_core.domain.model.SlackMessage;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.SlackMessageDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class SlackMessageServiceIT {
    @Autowired
    SlackMessageService slackMessageService;

    @Test
    void testCreate() {
        SlackMessageDto slackMessageDto = new SlackMessageDto("Test", "Content", "#000");
        StepVerifier
                .create(this.slackMessageService.create(slackMessageDto))
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateCloseCashierMessage() {
        SlackMessageDto slackMessageDto = new SlackMessageDto("Test", "Content", "#000");
        Cashier cashier = Cashier.builder().build();
        StepVerifier
                .create(this.slackMessageService.createCloseCahierMessage(cashier, slackMessageDto))
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateMessageFromDto() {
        SlackMessageDto slackMessageDto = new SlackMessageDto("Test", "Content", "#000");
        SlackMessage slackMessage = this.slackMessageService.createMessageFromDto(slackMessageDto);
        assertNotNull(slackMessage.getAttachments());
    }
}
