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

    public static final String NAME_ID = "/name";
    public static final String SEARCH = "/search";
    public static final String NAME = "/{name}";


    private StockAlarmService stockAlarmService;

    @Autowired
    public StockAlarmResource(StockAlarmService stockAlarmService) {
        this.stockAlarmService = stockAlarmService;
    }

    @PostMapping
    public Mono<StockAlarm> create(@RequestBody StockAlarm stockAlarm) {
        return null;
    }

    @PutMapping(NAME)
    public Mono<StockAlarm> update(@PathVariable String name, @RequestBody StockAlarm stockAlarm) {
        return null;
    }

    @GetMapping(NAME)
    public Flux<StockAlarm> find(@PathVariable String name) {
        return null;
    }


}
