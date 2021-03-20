package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.configuration.JwtService;
import es.upm.miw.betca_tpv_core.domain.model.Message;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.services.MessengerService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(MessengerResource.MESSENGER)
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
public class MessengerResource {

    public static final String MESSENGER = "/messenger";
    public static final String SENT_MESSAGES = "/sentMessages";
    public static final String RECEIVED_MESSAGES = "/receivedMessages";
    public static final String CHECK_NEW_MESSAGES = "/checkNewMessages";

    private MessengerService messengerService;
    private JwtService jwtService;

    @Autowired
    public MessengerResource(MessengerService messengerService, JwtService jwtService) {
        this.messengerService = messengerService;
        this.jwtService = jwtService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Message> create(@Valid @RequestBody Message newMessage) {
        newMessage.doDefault();
        return this.messengerService.create(newMessage);
    }

    @GetMapping(SENT_MESSAGES)
    public Flux<Message> getSentMessages(Authentication authentication) {
        String authenticatedUser = authentication.getName();

        return this.messengerService.getSentMessages(new User(authenticatedUser));
    }

    @GetMapping(RECEIVED_MESSAGES)
    public Flux<Message> getReceivedMessages(Authentication authentication) {
        String authenticatedUser = authentication.getName();

        return this.messengerService.getReceivedMessages(new User(authenticatedUser));
    }

    @GetMapping(CHECK_NEW_MESSAGES)
    public Flux<Message> checkNewMessages(Authentication authentication) {
        String authenticatedUser = authentication.getName();

        return this.messengerService.checkNewMessages(new User(authenticatedUser));
    }

}
