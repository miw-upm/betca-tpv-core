package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
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
public class SlackServiceIT {

    @Container
    public static WireMockContainer wireMockServer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromJSON(mockedSlackEndpoint());

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SlackService slackService;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("slack.webhook.url", () ->
                "http://localhost:" + wireMockServer.getPort() +"/slack");
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(slackService, "slackWebhookUrl",
                "http://localhost:" + wireMockServer.getPort() +"/slack");
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

    private static String mockedSlackEndpoint(){
        return "{\n" +
                "  \"request\": {\n" +
                "    \"method\": \"POST\",\n" +
                "    \"url\": \"/slack\"\n" +
                "  },\n" +
                "  \"response\": {\n" +
                "    \"status\": 200,\n" +
                "    \"headers\": {\n" +
                "      \"Content-Type\": \"application/json\"\n" +
                "    },\n" +
                "    \"body\": \"Success\"\n" +
                "  }\n" +
                "}";
    }
}
