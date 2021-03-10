package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketUnpaidDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CreditService {

    private CreditPersistence creditPersistence;

    @Autowired
    public CreditService(CreditPersistence creditPersistence) {
        this.creditPersistence = creditPersistence;
    }

    public Mono<Credit> create(Credit credit) {
        return this.creditPersistence.create(credit);
    }

    public Mono<Credit> findByUserReference(String userReference) {
        return this.creditPersistence.findByUserReference(userReference);
    }

    public Mono<Credit> addCreditSale(String userRef, CreditSale creditSale) {
        return this.creditPersistence.addCreditSale(userRef, creditSale);
    }

    public Mono<List<TicketUnpaidDto>> findUnpaidTicketsFromCreditLine(String userReference) {
        return this.creditPersistence.findUnpaidTicketsFromCreditLine(userReference);
    }

}
