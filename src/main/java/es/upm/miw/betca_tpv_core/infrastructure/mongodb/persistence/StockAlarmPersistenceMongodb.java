package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAlarmReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmLineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.ArrayList;
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
        if (!name.equals(stockAlarm.getName())) {
            return Mono.error(new ConflictException("Inconsistent stock alarm name"));
        }

        return this.stockAlarmReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent stock alarm name: " + name)))
                .flatMap(stockAlarmEntity -> {
                    stockAlarmEntity.setDescription(stockAlarm.getDescription());
                    stockAlarmEntity.setWarning(stockAlarm.getWarning());
                    stockAlarmEntity.setCritical(stockAlarm.getCritical());

                    return this.stockAlarmReactive.save(stockAlarmEntity);
                })
                .map(StockAlarmEntity::toStockAlarm);
    }

    @Override
    public Mono<StockAlarm> updateLines(String name, StockAlarm stockAlarm) {
        if (!name.equals(stockAlarm.getName())) {
            return Mono.error(new ConflictException("Inconsistent stock alarm name"));
        }

        return this.stockAlarmReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent stock alarm name: " + name)))
                .flatMap(stockAlarmEntity -> {
                    stockAlarmEntity.setDescription(stockAlarm.getDescription());
                    stockAlarmEntity.setWarning(stockAlarm.getWarning());
                    stockAlarmEntity.setCritical(stockAlarm.getCritical());

                    List<StockAlarmLine> newLines = stockAlarm.getStockAlarmLines();
                    if (newLines == null || newLines.isEmpty()) {
                        stockAlarmEntity.setStockAlarmLineEntities(new ArrayList<>());
                        return this.stockAlarmReactive.save(stockAlarmEntity).map(StockAlarmEntity::toStockAlarm);
                    }

                    return Flux.fromIterable(newLines)
                            .flatMap(line ->
                                    this.articleReactive.findByBarcode(line.getArticle().getBarcode())
                                            .switchIfEmpty(Mono.error(new NotFoundException(
                                                    "Non existent article barcode: " + line.getArticle().getBarcode())))
                                            .map(article ->
                                                    new StockAlarmLineEntity(article.toArticle(),
                                                            line.getWarning(), line.getCritical())))
                            .collectList()
                            .flatMap(updatedLines -> {
                                stockAlarmEntity.setStockAlarmLineEntities(updatedLines);
                                return this.stockAlarmReactive.save(stockAlarmEntity);
                            })
                            .map(StockAlarmEntity::toStockAlarm);
                });
    }

    @Override
    public Mono<StockAlarmLine[]> searchWarnings() {
        return this.stockAlarmReactive.findAll()
                .flatMap(stockAlarmEntity -> {
                    List<StockAlarmLineEntity> lines = stockAlarmEntity.getStockAlarmLineEntities();
                    return (lines == null || lines.isEmpty()) ? Flux.empty() : Flux.fromIterable(lines);
                })
                .flatMap(stockAlarmLineEntity -> this.articleReactive.findByBarcode(stockAlarmLineEntity.getArticle().getBarcode())
                        .map(article -> new AbstractMap.SimpleEntry<>(stockAlarmLineEntity, article.getStock()))
                        .defaultIfEmpty(new AbstractMap.SimpleEntry<>(stockAlarmLineEntity, null))
                )
                .filter(entry -> {
                    Integer stock = entry.getValue();
                    return stock != null && stock <= entry.getKey().getWarning();
                })
                .map(entry -> entry.getKey().toStockAlarmLine())
                .collectList()
                .map(list -> list.toArray(new StockAlarmLine[0]));
    }

    private Mono<Void> assertNameNotExist(String name) {
        return this.stockAlarmReactive.findByName(name)
                .flatMap(StockAlarmEntity -> Mono.error(
                   new ConflictException("Stock Alarm Name already exists : " + name)
                ));
    }
}
