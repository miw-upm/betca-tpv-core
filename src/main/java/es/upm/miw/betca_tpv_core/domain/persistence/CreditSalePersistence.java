package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditSalePersistence {

    Mono<CreditSale> create(CreditSale creditSale);
    Flux<CreditSale> findByPayed(Boolean payed);
    Mono<CreditSale> findByReference(String reference);
}
