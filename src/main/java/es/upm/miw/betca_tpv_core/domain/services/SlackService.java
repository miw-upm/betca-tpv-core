package es.upm.miw.betca_tpv_core.domain.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SlackService {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        if (slackWebhookUrl.isEmpty()) {
            throw new IllegalStateException("No Slack Webhook URL found. Please set the environment variable 'SLACK_URI'.");
        }
    }


    public void sendMessage(String level, String text) {

        String message = String.format("[%s] %s", level.toUpperCase(), text);


        Map<String, String> payload = new HashMap<>();
        payload.put("text", message);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);


        final var response = restTemplate.postForObject(slackWebhookUrl, request, String.class);
        log.info("slack Response {}", response);
    }
}


