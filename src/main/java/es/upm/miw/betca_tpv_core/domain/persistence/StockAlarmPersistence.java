package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockAlarmPersistence {

    Mono<StockAlarm> create(StockAlarm stockAlarm);

    Mono<StockAlarm> readByName(String name);

    Flux<StockAlarm> findAll();

    Mono<StockAlarm> update(String name, StockAlarm stockAlarm);

    Mono<StockAlarm> updateLines(String name, StockAlarm stockAlarm);
}
