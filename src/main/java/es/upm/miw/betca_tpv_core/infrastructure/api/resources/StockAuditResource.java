package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.services.StockAuditService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@Rest
@RequestMapping(StockAuditResource.AUDITS)
public class StockAuditResource {
    public static final String AUDITS = "/audits";
    public static final String OPENED = "/opened";

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
}
