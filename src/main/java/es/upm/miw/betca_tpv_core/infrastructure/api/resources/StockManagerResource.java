package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.StockManager;
import es.upm.miw.betca_tpv_core.domain.services.StockManagerService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockManagerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
@Rest
@RequestMapping(StockManagerResource.STOCK_MANAGER)
public class StockManagerResource {
    public static final String STOCK_MANAGER = "/stock-manager";
    public static final String STOCK = "/stock";
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
}
