package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketUnpaidDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CreditPersistence {

    Mono<Credit> create(Credit credit);
    Mono<Credit> findByUserReference(String userReference);
    Mono<Credit> addCreditSale(String userRef, CreditSale creditSale);
    Mono<List<TicketUnpaidDto>> findUnpaidTicketsFromCreditLine(String userRef);
}
