package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAlarmReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class StockAlarmPersistenceMongodb implements StockAlarmPersistence {

    private final StockAlarmReactive stockAlarmReactive;

    @Autowired
    public StockAlarmPersistenceMongodb(StockAlarmReactive stockAlarmReactive) {
        this.stockAlarmReactive = stockAlarmReactive;
    }

    @Override
    public Mono<StockAlarm> create(StockAlarm stockAlarm) {
        return this.assertNameNotExist(stockAlarm.getName())
                .then(Mono.just(new StockAlarmEntity(stockAlarm)))
                .flatMap(this.stockAlarmReactive::save)
                .map(StockAlarmEntity::toStockAlarm);
    }

    private Mono<Void> assertNameNotExist(String name) {
        return this.stockAlarmReactive.findByName(name)
                .flatMap(StockAlarmEntity -> Mono.error(
                   new ConflictException("Stock Alarm Name already exists : " + name)
                ));
    }
}
