package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
}
