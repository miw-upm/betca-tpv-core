package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderInvoiceEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ProviderInvoiceReactive extends ReactiveSortingRepository<ProviderInvoiceEntity, String> {

    Mono< ProviderInvoiceEntity > findByNumber(Integer number);

    @Query("{'creationDate': {$gte: ?0, $lte: ?1}}")
    Flux< ProviderInvoiceEntity > findByCreationDateBetweenInclusive(LocalDate startDate, LocalDate endDate);

}
