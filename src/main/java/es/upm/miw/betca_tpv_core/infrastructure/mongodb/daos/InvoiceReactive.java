package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.InvoiceEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface InvoiceReactive extends ReactiveSortingRepository<InvoiceEntity, String> {
}
