package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.persistence.CustomerDiscountPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CustomerDiscountReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class CustomerDiscountPersistenceMongodb implements CustomerDiscountPersistence {

    private CustomerDiscountReactive customerDiscountReactive;

    @Autowired
    public CustomerDiscountPersistenceMongodb(CustomerDiscountReactive customerDiscountReactive) {
        this.customerDiscountReactive = customerDiscountReactive;
    }

    @Override
    public Flux<CustomerDiscount> findByUserPhone(String userPhone) {
        return this.customerDiscountReactive.findByUserPhone(userPhone)
                .map(CustomerDiscountEntity::toCustomerDiscount);
    }
}
