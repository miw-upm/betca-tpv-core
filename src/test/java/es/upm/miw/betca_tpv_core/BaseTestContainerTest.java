package es.upm.miw.betca_tpv_core;


import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.wiremock.integrations.testcontainers.WireMockContainer;

public class BaseTestContainerTest {

    @Container
    public static WireMockContainer wireMockServer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromJSON(mockedSlackEndpoint());

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("slack.webhook.url", () ->"http://localhost");
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
