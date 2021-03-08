package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface StockAlarmReactive extends ReactiveSortingRepository<StockAlarmEntity, String> {
    Flux<StockAlarmEntity> findByNameLike(String name);
}
