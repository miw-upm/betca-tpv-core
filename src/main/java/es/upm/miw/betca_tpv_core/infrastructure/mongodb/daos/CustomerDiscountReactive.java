package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerDiscountReactive extends ReactiveMongoRepository<CustomerDiscountEntity, String> {

    Mono<CustomerDiscountEntity> readByUserMobile(String userMobile);

    Mono<Void> deleteByUserMobile(String userMobile);
}
