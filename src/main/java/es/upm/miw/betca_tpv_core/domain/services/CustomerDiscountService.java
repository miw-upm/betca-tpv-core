package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.persistence.CustomerDiscountPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerDiscountService {

    private final CustomerDiscountPersistence customerDiscountPersistence;

    @Autowired
    public CustomerDiscountService(CustomerDiscountPersistence customerDiscountPersistence){
        this.customerDiscountPersistence = customerDiscountPersistence;
    }

    public Mono<CustomerDiscount> createCustomerDiscount(CustomerDiscount customerDiscount) {
        return this.customerDiscountPersistence.createCustomerDiscount(customerDiscount);
    }

    public Flux<CustomerDiscount> findAll() {
        return this.customerDiscountPersistence.findAll();
    }
}
