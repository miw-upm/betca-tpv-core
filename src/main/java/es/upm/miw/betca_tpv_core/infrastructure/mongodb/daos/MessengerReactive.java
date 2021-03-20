package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.MessageEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface MessengerReactive extends ReactiveSortingRepository<MessageEntity, String> {
    Flux<MessageEntity> findByUserFrom(String userFrom);

    Flux<MessageEntity> findByUserTo(String userTo);

    Flux<MessageEntity> findByUserToAndIsRead(String userTo, Boolean isRead);
}
