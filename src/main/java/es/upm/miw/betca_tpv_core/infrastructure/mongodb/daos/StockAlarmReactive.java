package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockAlarmReactive extends ReactiveSortingRepository<StockAlarmEntity, String> {
    Flux<StockAlarmEntity> findByNameLike(String name);

    Mono<StockAlarmEntity> findByName(String name);

    Mono<Void> deleteByName(String name);

}
