package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
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

import java.util.stream.Stream;

@Repository
public class StockAlarmPersistenceMongodb implements StockAlarmPersistence {

    private StockAlarmReactive stockAlarmReactive;
    private ArticleReactive articleReactive;

    @Autowired
    public StockAlarmPersistenceMongodb(StockAlarmReactive stockAlarmReactive, ArticleReactive articleReactive) {
        this.articleReactive = articleReactive;
        this.stockAlarmReactive = stockAlarmReactive;
    }

    @Override
    public Mono<StockAlarm> create(StockAlarm stockAlarm) {
        StockAlarmEntity stockAlarmEntity = new StockAlarmEntity(stockAlarm);
        return this.updateAlarmLineList(stockAlarmEntity, stockAlarm)
                .then(this.stockAlarmReactive.save(stockAlarmEntity)
                        .map(StockAlarmEntity::toStockAlarm));
    }

    @Override
    public Flux<StockAlarm> findByNameLike(String name) {
        return this.stockAlarmReactive.findByNameLike(name)
                .map(StockAlarmEntity::toStockAlarm);
    }

    @Override
    public Mono<StockAlarm> update(String name, StockAlarm stockAlarm) {

        Mono<StockAlarmEntity> stockAlarmEntity = this.stockAlarmReactive.findByName(name);
        return stockAlarmEntity
                .switchIfEmpty(Mono.error(new NotFoundException("No found stock-alarm name: " + name)))
                .flatMap(stockAlarmEntityMap -> {
                    BeanUtils.copyProperties(stockAlarm, stockAlarmEntityMap);
                    return this.updateAlarmLineList(stockAlarmEntityMap, stockAlarm)
                            .then(this.stockAlarmReactive.save(stockAlarmEntityMap));
                })
                .map(StockAlarmEntity::toStockAlarm);
    }


    @Override
    public Mono<StockAlarm> readByName(String name) {
        return this.stockAlarmReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent stock-alarm name: " + name)))
                .map(StockAlarmEntity::toStockAlarm);
    }

    @Override
    public Mono<Void> delete(String name) {
        return this.stockAlarmReactive.deleteByName(name);
    }

    @Override
    public Flux<StockAlarmLine> findAllStockAlarmLinesWithoutNull() {
        return this.stockAlarmReactive.findAll()
                .map(StockAlarmEntity::toStockAlarm)
                .flatMap(stockAlarm -> Flux.fromStream(stockAlarm.getStockAlarmLines().stream())
                        .map(stockAlarmLine -> {
                            stockAlarmLine.setWarning(stockAlarmLine.getWarning() == null ? stockAlarm.getWarning() : stockAlarmLine.getWarning());
                            stockAlarmLine.setCritical(stockAlarmLine.getCritical() == null ? stockAlarm.getCritical() : stockAlarmLine.getCritical());
                            return stockAlarmLine;
                        })
                );
    }

    private Mono<Void> updateAlarmLineList(StockAlarmEntity stockAlarmEntity, StockAlarm stockAlarm) {
        stockAlarmEntity.clearAlarms();
        return Flux.fromStream(stockAlarm.getStockAlarmLines() == null ?
                Stream.empty() :
                stockAlarm.getStockAlarmLines().stream())
                .flatMap(stockAlarmLine -> {
                    StockAlarmLineEntity stockAlarmLineEntity = new StockAlarmLineEntity(stockAlarmLine);

                    return this.articleReactive.findByBarcode(stockAlarmLine.getBarcode())
                            .switchIfEmpty(Mono.error(new NotFoundException("Article: " + stockAlarmLine.getBarcode())))
                            .map(articleEntity -> {
                                stockAlarmLineEntity.setArticleEntity(articleEntity);
                                return stockAlarmLineEntity;
                            });

                }).doOnNext(stockAlarmEntity::add)
                .then();
    }

}
