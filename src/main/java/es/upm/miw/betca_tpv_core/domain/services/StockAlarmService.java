package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StockAlarmService {

    private StockAlarmPersistence stockAlarmPersistence;

    @Autowired
    public StockAlarmService(StockAlarmPersistence stockAlarmPersistence) {
        this.stockAlarmPersistence = stockAlarmPersistence;
    }

    public Mono<StockAlarm> create(StockAlarm stockAlarm) {
        return this.stockAlarmPersistence.create(stockAlarm);
    }
}
