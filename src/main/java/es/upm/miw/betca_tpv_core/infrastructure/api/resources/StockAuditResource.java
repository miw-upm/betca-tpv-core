package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.services.StockAuditService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Rest
@RequestMapping(StockAuditResource.AUDITS)
public class StockAuditResource {
    public static final String AUDITS = "/audits";
    public static final String OPENED = "/opened";
    public static final String AUDIT_ID = "/{id}";
    public static final String CLOSE = "/close";

    private StockAuditService stockAuditService;

    @Autowired
    public StockAuditResource(StockAuditService stockAuditService) {
        this.stockAuditService = stockAuditService;
    }

    @GetMapping(OPENED)
    public Mono<StockAuditDto> findSingleOpenedAudit() {
        return this.stockAuditService
                .findSingleOpenedAudit()
                .map(StockAuditDto::new);
    }

    @PostMapping(produces = {"application/json"})
    public Mono<StockAuditDto> create(@RequestBody List<String> barcodesWithoutAudit) {
        return this.stockAuditService
                .create(barcodesWithoutAudit)
                .map(StockAuditDto::new);
    }

    @PutMapping(AUDIT_ID)
    public Mono<StockAuditDto> update(@PathVariable String id, @RequestBody StockAuditDto stockAuditDto) {
        return this.stockAuditService.update(id, stockAuditDto);
    }

    @PutMapping(CLOSE + AUDIT_ID)
    public Mono<StockAuditDto> close(@PathVariable String id, @RequestBody StockAuditDto stockAuditDto) {
        return this.stockAuditService.close(id, stockAuditDto);
    }
}
