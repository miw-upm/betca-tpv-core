package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class StockAuditService {
    private StockAuditPersistence stockAuditPersistence;

    @Autowired
    public StockAuditService(StockAuditPersistence stockAuditPersistence) {
        this.stockAuditPersistence = stockAuditPersistence;
    }

    public Mono<StockAudit> findSingleOpenedAudit() {
        return this.stockAuditPersistence.findFirstByCloseDateNull();
    }

    public Mono<StockAudit> create(List<String> barcodesWithoutAudit) {
        return this.stockAuditPersistence.create(barcodesWithoutAudit);
    }

    public Mono<StockAuditDto> update(String id, StockAuditDto stockAuditDto) {
        return this.stockAuditPersistence.update(id, stockAuditDto);
    }
}
