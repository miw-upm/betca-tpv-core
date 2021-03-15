package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Message;
import es.upm.miw.betca_tpv_core.domain.services.MessengerService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(MessengerResource.MESSENGER)
public class MessengerResource {

    public static final String MESSENGER = "/messenger";

    private MessengerService messengerService;

    @Autowired
    public MessengerResource(MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Message> create(@Valid @RequestBody Message newMessage) {
        newMessage.doDefault();
        return this.messengerService.create(newMessage);
    }

}
