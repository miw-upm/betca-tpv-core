package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderInvoiceEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface ProviderInvoiceReactive extends ReactiveSortingRepository<ProviderInvoiceEntity, String> {

    Mono< ProviderInvoiceEntity > findByNumber(Integer number);
}
