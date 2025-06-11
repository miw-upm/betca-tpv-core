package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StockAuditPersistence {
    Flux<StockAudit> findAll();

    Mono<StockAudit> read(String id);

    Mono<StockAudit> create(StockAudit stockAudit);  // Cambiado de save a create

    Mono<StockAudit> update(StockAudit stockAudit);  // Actualizado para manejar el objeto completo

    Mono<Void> delete(String id);

    // Método específico para cerrar auditoría si es necesario
    default Mono<Void> close(StockAudit stockAudit) {
        return update(stockAudit).then();
    }
}