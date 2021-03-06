package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditSalePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditSaleService {
    private CreditSalePersistence creditSalePersistence;

    @Autowired
    public CreditSaleService(CreditSalePersistence creditSalePersistence) {
        this.creditSalePersistence = creditSalePersistence;
    }

    public Mono<CreditSale> create(CreditSale creditSale) {
        return this.creditSalePersistence.create(creditSale);
    }

    public Flux<CreditSale> findByPayed(Boolean payed) {
        return this.creditSalePersistence.findByPayed(payed);
    }
}
