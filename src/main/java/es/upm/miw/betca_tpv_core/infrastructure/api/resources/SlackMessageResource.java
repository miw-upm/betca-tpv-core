package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.SlackMessage;
import es.upm.miw.betca_tpv_core.domain.services.SlackMessageService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(SlackMessageResource.SLACK_MESSAGES)
public class SlackMessageResource {
    public static final String SLACK_MESSAGES = "slack-messages";

    private final SlackMessageService slackMessageService;

    @Autowired
    SlackMessageResource(SlackMessageService slackMessageService) {
        this.slackMessageService = slackMessageService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<SlackMessage> create(@Valid @RequestBody SlackMessage slackMessage) {
        return Mono.just(slackMessage);
    }
}
