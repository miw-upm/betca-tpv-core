package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.SlackMessage;
import es.upm.miw.betca_tpv_core.domain.services.SlackMessageService;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.SlackMessageDto;
import es.upm.miw.betca_tpv_core.infrastructure.slack_client.SlackMessagePublisherClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.SlackMessageResource.SLACK_MESSAGES;

@RestTestConfig
public class SlackMessageResourceIT {

    @MockBean
    SlackMessagePublisherClient slackMessagePublisherClient;

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
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

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(SLACK_MESSAGES)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(slackMessageDto), SlackMessageDto.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateUnauthorized() {
        Mockito.when(slackMessagePublisherClient.create(slackMessage))
                .thenReturn(Mono.just(slackMessage));

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(SLACK_MESSAGES)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(slackMessageDto), SlackMessageDto.class)
                .exchange()
                .expectStatus().isOk();
    }
}
