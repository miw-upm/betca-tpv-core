package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface StockAlarmReactive extends ReactiveMongoRepository<StockAlarmEntity, String> {
    Mono<StockAlarmEntity> findByName(String name);
}
