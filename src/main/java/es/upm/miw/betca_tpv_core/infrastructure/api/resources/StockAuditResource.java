package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.services.StockAuditService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

@Rest
@RequestMapping(StockAuditResource.STOCK_AUDIT)
public class StockAuditResource {

    public static final String STOCK_AUDIT = "/stock-audits";
    private final StockAuditService stockAuditService;

    @Autowired
    public StockAuditResource(StockAuditService stockAuditService) {
        this.stockAuditService = stockAuditService;
    }

    @GetMapping
    public Flux<StockAudit> findAll() {
        return stockAuditService.findAll();
    }

}