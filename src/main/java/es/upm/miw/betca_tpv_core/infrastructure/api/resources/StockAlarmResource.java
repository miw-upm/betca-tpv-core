package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.services.StockAlarmService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(StockAlarmResource.STOCK_ALARMS)
public class StockAlarmResource {

    public static final String STOCK_ALARMS = "/stock-alarms";
    public static final String STOCK_ALARM_ID = "/{name}";
    public static final String STOCK_ALARM_LINES = "/lines";

    private final StockAlarmService stockAlarmService;

    @Autowired
    public StockAlarmResource(StockAlarmService stockAlarmService) {
        this.stockAlarmService = stockAlarmService;
    }

    @PostMapping
    public Mono<StockAlarm> create(@Valid @RequestBody StockAlarm stockAlarm) {
        return this.stockAlarmService.create(stockAlarm);
    }

    @GetMapping(STOCK_ALARM_ID)
    public Mono<StockAlarm> read(@PathVariable String name) {
        return this.stockAlarmService.read(name);
    }

    @GetMapping
    public Flux<StockAlarm> findAll() {
        return this.stockAlarmService.findAll()
                .map(StockAlarm::ofNameDescriptionWarningCritical);
    }

    @PutMapping(STOCK_ALARM_ID)
    public Mono<StockAlarm> update(@PathVariable String name, @Valid @RequestBody StockAlarm stockAlarm) {
        return this.stockAlarmService.update(name, stockAlarm)
                .map(StockAlarm::ofNameDescriptionWarningCritical);
    }

    @PutMapping(STOCK_ALARM_ID+STOCK_ALARM_LINES)
    public Mono<StockAlarm> updateLines(@PathVariable String name, @Valid @RequestBody StockAlarm stockAlarm) {
        return this.stockAlarmService.updateLines(name, stockAlarm);
    }
}
