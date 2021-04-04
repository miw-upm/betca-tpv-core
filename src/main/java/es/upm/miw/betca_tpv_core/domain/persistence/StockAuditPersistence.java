package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditDto;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface StockAuditPersistence {
    Mono<StockAudit> findFirstByCloseDateNull();

    Mono<StockAudit> create(List<String> barcodesWithoutAudit);

    Mono<StockAuditDto> update(String id, StockAuditDto stockAuditDto, Boolean close);
}
