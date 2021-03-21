package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.persistence.CustomerDiscountPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerDiscountService {

    private CustomerDiscountPersistence customerDiscountPersistence;

    @Autowired
    public CustomerDiscountService(CustomerDiscountPersistence customerDiscountPersistence) {
        this.customerDiscountPersistence = customerDiscountPersistence;
    }

    public Flux<CustomerDiscount> findByUser(String user) {
        return this.customerDiscountPersistence.findByUser(user);
    }

    public Flux<CustomerDiscount> findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(String note, Double discount, Double minimumPurchase, String user) {
        return this.customerDiscountPersistence.findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(note, discount, minimumPurchase, user);
    }

    public Mono<CustomerDiscount> create(CustomerDiscount customerDiscount) {
        return this.customerDiscountPersistence.create(customerDiscount);
    }

}
