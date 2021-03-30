package es.upm.miw.betca_tpv_core.infrastructure.slack_client;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadGatewayException;
import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.exceptions.SlackUriException;
import es.upm.miw.betca_tpv_core.domain.model.SlackMessage;
import es.upm.miw.betca_tpv_core.domain.slack.SlackMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SlackMessagePublisherClient implements SlackMessagePublisher {
    private String slackUri;
    private WebClient.Builder webClientBuilder;

    @Autowired
    private Environment env;

    @Autowired
    public SlackMessagePublisherClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<SlackMessage> create(SlackMessage slackMessage) {
        try {
            this.slackUri = this.env != null ? this.env.getProperty("miw.slack.uri") : null;
        } catch (Exception e) {
            return Mono.error(new SlackUriException("URI not configured."));
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getCredentials())
                .flatMap(token -> webClientBuilder.build()
                        .mutate().defaultHeader("Authorization", "Bearer " + token).build()
                        .post()
                        .uri(slackUri)
                        .body(Mono.just(slackMessage), SlackMessage.class)
                        .exchange())
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException("Unexpected error. Slack message. " + exception.getMessage())))
                .flatMap(response -> {
                    if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                        return Mono.error(new ForbiddenException("Forbidden"));
                    } else if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("NotFound"));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException("Unexpected error: Slack."));
                    } else {
                        return Mono.just(slackMessage);
                    }
                });
    }
}
