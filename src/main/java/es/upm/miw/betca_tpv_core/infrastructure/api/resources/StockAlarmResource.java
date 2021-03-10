package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.services.StockAlarmService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(StockAlarmResource.STOCK_ALARMS)
public class StockAlarmResource {
    public static final String STOCK_ALARMS = "/stock-alarms";

    public static final String NAME_ID = "/{name}";
    public static final String SEARCH = "/search";
    public static final String NAME = "/name";

    private StockAlarmService stockAlarmService;

    @Autowired
    public StockAlarmResource(StockAlarmService stockAlarmService) {
        this.stockAlarmService = stockAlarmService;
    }

    @PostMapping
    public Mono<StockAlarm> create(@RequestBody StockAlarm stockAlarm) {
        return this.stockAlarmService.create(stockAlarm);
    }

    @PutMapping(NAME_ID)
    public Mono<StockAlarm> update(@PathVariable String name, @RequestBody StockAlarm stockAlarm) {
        stockAlarm.doDefault();
        return this.stockAlarmService.update(name, stockAlarm);
    }

    @GetMapping(NAME_ID)
    public Mono<StockAlarm> read(@PathVariable String name) {
        return this.stockAlarmService.read(name);
    }

    @GetMapping(NAME)
    public Flux<StockAlarm> find(@RequestParam String name) {
        return this.stockAlarmService.findByNameLike(name);
    }

    @DeleteMapping(NAME_ID)
    public Mono<Void> delete(@PathVariable String name) {
        return this.stockAlarmService.delete(name);
    }

}
