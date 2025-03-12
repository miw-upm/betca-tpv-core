package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface OrderReactive extends ReactiveMongoRepository<OrderEntity, String> {

    Mono<OrderEntity> findByReference(String reference);

}
