package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAlarmsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Flux<StockAlarm> findByNameLike(String name) {
        return this.stockAlarmPersistence.findByNameLike(name);
    }

    public Mono<StockAlarm> update(String name, StockAlarm stockAlarm) {
        return this.stockAlarmPersistence.update(name, stockAlarm);
    }

    public Mono<StockAlarm> read(String name) {
        return this.stockAlarmPersistence.readByName(name);
    }

    public Mono<Void> delete(String name) {
        return this.stockAlarmPersistence.delete(name);
    }

    public Mono<StockAlarmsDto> findStockAlarms(List<String> alarms) {
        StockAlarmsDto stockAlarmsDto = new StockAlarmsDto();
        return
                this.stockAlarmPersistence.findAllStockAlarmLinesWithoutNull()
                        .doOnNext(stockAlarmLine -> {
                            if (isCriticalAlarm(stockAlarmLine) && alarms.contains("CRITICAL")) {
                                stockAlarmsDto.addCritical(stockAlarmLine);
                            } else if (isWarningAlarm(stockAlarmLine) && alarms.contains("WARNING")) {
                                stockAlarmsDto.addWarning(stockAlarmLine);
                            }
                        }).then(Mono.just(stockAlarmsDto));
    }

    private boolean isCriticalAlarm(StockAlarmLine stockAlarmLine) {
        return stockAlarmLine.getStock() < stockAlarmLine.getCritical();
    }

    private boolean isWarningAlarm(StockAlarmLine stockAlarmLine) {
        return stockAlarmLine.getStock() < stockAlarmLine.getWarning();
    }

}
