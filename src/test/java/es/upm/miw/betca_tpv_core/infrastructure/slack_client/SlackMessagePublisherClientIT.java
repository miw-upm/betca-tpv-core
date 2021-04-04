package es.upm.miw.betca_tpv_core.infrastructure.slack_client;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import es.upm.miw.betca_tpv_core.domain.model.SlackMessage;
import es.upm.miw.betca_tpv_core.domain.services.SlackMessageService;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.SlackMessageDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.resources.RestTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class SlackMessagePublisherClientIT {

    @MockBean
    SlackMessagePublisherClient slackMessagePublisherClient;
    @Autowired
    private SlackMessageService slackMessageService;
    private SlackMessage slackMessage;
    private SlackMessageDto slackMessageDto;

    @BeforeEach
    void init() {
        this.slackMessageDto = new SlackMessageDto("Test", "Content", "#000");
        this.slackMessage = this.slackMessageService.createMessageFromDto(this.slackMessageDto);
    }

    @Test
    void testCreate() {
        Mockito.when(slackMessagePublisherClient.create(slackMessage))
                .thenReturn(Mono.just(slackMessage));

        Mono<SlackMessage> slackMessage = this.slackMessagePublisherClient.create(this.slackMessage);
        assertNotNull(slackMessage);
    }

    @Test
    void testCreateCloseCashierMessage() {
        Cashier cashier = Cashier.builder().build();
        Mockito.when(slackMessagePublisherClient.createCloseCashierMessage(cashier, slackMessage))
                .thenReturn(Mono.just(cashier));

        Mono<Cashier> cashierMono = this.slackMessagePublisherClient.createCloseCashierMessage(cashier, this.slackMessage);
        assertNotNull(cashierMono);
    }
}
