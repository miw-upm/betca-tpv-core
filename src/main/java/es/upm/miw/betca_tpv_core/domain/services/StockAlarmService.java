package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockAlarmService {

    private final StockAlarmPersistence stockAlarmPersistence;

    @Autowired
    public StockAlarmService(StockAlarmPersistence stockAlarmPersistence) {
        this.stockAlarmPersistence = stockAlarmPersistence;
    }

    public Mono<StockAlarm> create(StockAlarm stockAlarm) {
        return this.stockAlarmPersistence.create(stockAlarm);
    }

    public Mono<StockAlarm> read(String name) {
        return this.stockAlarmPersistence.readByName(name);
    }

    public Flux<StockAlarm> findAll() {
        return this.stockAlarmPersistence.findAll();
    }

    public Mono<StockAlarm> update(String name, StockAlarm stockAlarm) {
        return this.stockAlarmPersistence.update(name, stockAlarm);
    }

    public Mono<StockAlarm> updateLines(String name, StockAlarm stockAlarm) {
        return this.stockAlarmPersistence.updateLines(name, stockAlarm);
    }

    public Mono<StockAlarmLine[]> searchWarnings() {
        return this.stockAlarmPersistence.searchWarnings();
    }

    public Mono<StockAlarmLine[]> searchCriticals() {
        return this.stockAlarmPersistence.searchCriticals();
    }
}
