package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.BaseTestContainerTest;
import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;



@TestConfig
@Testcontainers
@SpringJUnitConfig
public class SlackServiceIT extends BaseTestContainerTest {

    @InjectMocks
    private SlackService slackService;


    @BeforeEach
    void setUpContainer () {
        ReflectionTestUtils.setField(slackService, "slackWebhookUrl",
                "http://localhost:" + wireMockServer.getPort() +"/slack");
    }

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessage() {
        StepVerifier
                .create(Mono.fromCallable(() -> {
                    slackService.sendMessage("info", "Mensaje de prueba");
                    return "done";
                }))
                .expectNext("done")
                .verifyComplete();

    }

    @Test
    void testInitWithEmptyWebhookUrlThrowsException() {

        ReflectionTestUtils.setField(slackService, "slackWebhookUrl", "");


        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> slackService.init()
        );
    }




}
