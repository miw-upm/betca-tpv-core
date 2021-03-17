package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Message;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MessengerPersistence {

    Mono<Message> create(Message newMessage);

    Flux<Message> findByUserFromNullSafe(User userFrom);

    Flux<Message> findByUserToNullSafe(User userFrom);

    Flux<Message> findNotReadMessages(User userFrom);

}
