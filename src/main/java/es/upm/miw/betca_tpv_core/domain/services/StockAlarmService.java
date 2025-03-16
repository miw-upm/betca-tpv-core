package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
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
}
