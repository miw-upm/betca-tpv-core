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

    private final StockAlarmService stockAlarmService;

    @Autowired
    public StockAlarmResource(StockAlarmService stockAlarmService) {
        this.stockAlarmService = stockAlarmService;
    }

    @PostMapping
    public Mono<StockAlarm> create(@Valid @RequestBody StockAlarm stockAlarm) {
        return this.stockAlarmService.create(stockAlarm);
    }

    @GetMapping
    public Mono<StockAlarm> read(@PathVariable String name) {
        return this.stockAlarmService.read(name);
    }

    @GetMapping
    public Flux<StockAlarm> findAll() {
        return this.stockAlarmService.findAll();
    }
}
