package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import reactor.core.publisher.Mono;

public interface CreditPersistence {

    Mono<Credit> create(Credit credit);
    Mono<Credit> findByUserReference(String userReference);
    Mono<Credit> addCreditSale(String userRef, CreditSale creditSale);

}