package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Message;
import es.upm.miw.betca_tpv_core.domain.persistence.MessengerPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MessengerService {

    private MessengerPersistence messengerPersistence;

    @Autowired
    public MessengerService(MessengerPersistence messengerPersistence) {
        this.messengerPersistence = messengerPersistence;
    }

    public Mono<Message> create(Message newMessage) {
        return this.messengerPersistence.create(newMessage);
    }

}