package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerPointsEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerPointsReactive extends ReactiveMongoRepository<CustomerPointsEntity, String> {
    Mono<CustomerPointsEntity> readCustomerPointsByUserMobileNumber(String userMobileNumber);
}
