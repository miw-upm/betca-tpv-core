package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.MessageEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface MessengerReactive extends ReactiveSortingRepository<MessageEntity, String> {

}
