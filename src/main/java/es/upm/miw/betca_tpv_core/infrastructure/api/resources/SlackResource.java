package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.services.SlackService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.SlackMessageDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(SlackResource.SLACK)
public class SlackResource {
    public static final String SLACK = "/slack";

    private final SlackService slackService;

    @Autowired
    public SlackResource(SlackService slackService) {
        this.slackService = slackService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/publish")
    public Mono<Void> sendMessage(@RequestBody SlackMessageDto slackMessageDto) {
        return Mono.fromRunnable(() ->
                slackService.sendMessage(slackMessageDto.getLevel(), slackMessageDto.getText())
        );
    }
}
