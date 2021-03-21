package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StockAlarmPersistence {

    Mono<StockAlarm> create(StockAlarm stockAlarm);

    Flux<StockAlarm> findByNameLike(String name);

    Mono<StockAlarm> update(String name, StockAlarm stockAlarm);

    Mono<StockAlarm> readByName(String name);

    Mono<Void> delete(String name);

    Flux<StockAlarmLine> findAllStockAlarmLinesWithoutNull();
}
