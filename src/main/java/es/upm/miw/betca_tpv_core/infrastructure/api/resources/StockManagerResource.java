package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.StockManager;
import es.upm.miw.betca_tpv_core.domain.services.StockManagerService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockManagerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;

@Rest
@RequestMapping(StockManagerResource.STOCK_MANAGER)
public class StockManagerResource {
    public static final String STOCK_MANAGER = "/stock-manager";
    public static final String STOCK = "/stock";
    public static final String STOCK_SOLD = "/stock-sold";
    public static final String STOCK_FUTURE = "/stock-future";

    private StockManagerService stockManagerService;

    @Autowired
    public StockManagerResource(StockManagerService stockManagerService) {
        this.stockManagerService = stockManagerService;
    }
    @GetMapping(STOCK)
    public Flux<StockManagerDto> searchProductsByStock(
            @RequestParam(required = true) Integer stock) {
        return this.stockManagerService.searchProductsByStock(stock)
                .map(StockManagerDto::new);
    }

    @GetMapping(STOCK_SOLD)
    public Flux<StockManagerDto> searchSoldProducts(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime initial,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return this.stockManagerService.searchSoldProducts(initial, end)
                .map(StockManagerDto::new);
    }
    @GetMapping(STOCK_FUTURE)
    public Mono<StockManagerDto> searchFutureStock(@RequestParam(required = true) String barcode) {
        return this.stockManagerService.searchFutureStock(barcode)
                .map(StockManagerDto::new);

    }
}
