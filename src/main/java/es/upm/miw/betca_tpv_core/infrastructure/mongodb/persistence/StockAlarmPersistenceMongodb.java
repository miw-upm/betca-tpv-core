package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAlarmReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmLineEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class StockAlarmPersistenceMongodb implements StockAlarmPersistence {

    private final StockAlarmReactive stockAlarmReactive;
    private final ArticleReactive articleReactive;

    @Autowired
    public StockAlarmPersistenceMongodb(StockAlarmReactive stockAlarmReactive, ArticleReactive articleReactive) {
        this.stockAlarmReactive = stockAlarmReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Mono<StockAlarm> create(StockAlarm stockAlarm) {
        return this.assertNameNotExist(stockAlarm.getName())
                .then(Mono.just(new StockAlarmEntity(stockAlarm)))
                .flatMap(this.stockAlarmReactive::save)
                .map(StockAlarmEntity::toStockAlarm);
    }

    @Override
    public Mono<StockAlarm> readByName(String name) {
        return this.stockAlarmReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent Stock Alarm name: " + name)))
                .map(StockAlarmEntity::toStockAlarm);
    }

    @Override
    public Flux<StockAlarm> findAll() {
        return this.stockAlarmReactive.findAll().map(StockAlarmEntity::toStockAlarm);
    }

    @Override
    public Mono<StockAlarm> update(String name, StockAlarm stockAlarm) {
        if(!name.equals(stockAlarm.getName())) {
            return Mono.error(new ConflictException("Inconsistent stock alarm name"));
        }
        return this.stockAlarmReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent stock alarm name: " + name)))
                .flatMap(stockAlarmEntity -> {
                    BeanUtils.copyProperties(stockAlarm, stockAlarmEntity);
                    List<StockAlarmLineEntity> lines = stockAlarmEntity.getStockAlarmLineEntityList();
                    if (lines == null || lines.isEmpty()) {
                        return Mono.just(stockAlarmEntity);
                    }
                    return Flux.fromIterable(lines)
                            .flatMap(line -> this.articleReactive.findByBarcode(line.getArticle().getBarcode())
                                    .switchIfEmpty(
                                            Mono.error(new NotFoundException("Non existent article barcode: " +
                                                    line.getArticle().getBarcode()))))
                            .then(Mono.just(stockAlarmEntity));
                })
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
