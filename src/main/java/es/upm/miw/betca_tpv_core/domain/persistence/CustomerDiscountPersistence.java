package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerDiscountPersistence {

    Mono<CustomerDiscount> createCustomerDiscount(CustomerDiscount customerDiscount);

    Mono<CustomerDiscount> readByUserMobile(String userMobile);

    Mono<CustomerDiscount> updateCustomerDiscount(String userMobile, CustomerDiscount customerDiscount);

    Flux<CustomerDiscount> findAll();
}
