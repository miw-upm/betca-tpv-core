package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAlarmReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmLineEntity;
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

        return Flux.fromStream(stockAlarm.getStockAlarmLines() == null ?
                Stream.empty() : stockAlarm.getStockAlarmLines().stream())
                .flatMap(stockAlarmLine -> {
                    StockAlarmLineEntity stockAlarmLineEntity = new StockAlarmLineEntity(stockAlarmLine);
                    return this.articleReactive.findByBarcode(stockAlarmLine.getBarcode())
                            .switchIfEmpty(Mono.error(new NotFoundException("Article: " + stockAlarmLine.getBarcode())))
                            .map(articleEntity -> {
                                stockAlarmLineEntity.setArticleEntity(articleEntity);
                                return stockAlarmLineEntity;
                            });
                }).doOnNext(stockAlarmEntity::add)
                .then(this.stockAlarmReactive.save(stockAlarmEntity))
                .map(StockAlarmEntity::toStockAlarm);
    }

    @Override
    public Flux<StockAlarm> findByNameLike(String name) {
        return this.stockAlarmReactive.findByNameLike(name)
                .map(StockAlarmEntity::toStockAlarm);
    }

    @Override
    public Mono<StockAlarm> update(StockAlarm stockAlarm) {
        return null;
    }

    @Override
    public Mono<StockAlarm> readByName(String name) {
        return this.stockAlarmReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent stock-alarm name: " + name)))
                .map(StockAlarmEntity::toStockAlarm);
    }
}
