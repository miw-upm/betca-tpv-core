package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.CustomerDiscountPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CustomerDiscountReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CustomerDiscountPersistenceMongodb implements CustomerDiscountPersistence {

    private CustomerDiscountReactive customerDiscountReactive;
    private UserMicroservice userMicroservice;

    @Autowired
    public CustomerDiscountPersistenceMongodb(CustomerDiscountReactive customerDiscountReactive, UserMicroservice userMicroservice) {
        this.customerDiscountReactive = customerDiscountReactive;
        this.userMicroservice = userMicroservice;
    }

    @Override
    public Flux<CustomerDiscount> findByUser(String user) {
        return this.customerDiscountReactive.findByUser(user)
                .map(CustomerDiscountEntity::toCustomerDiscount);
    }

    @Override
    public Flux<CustomerDiscount> findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(String note, Double discount, Double minimumPurchase, String user) {
        return this.customerDiscountReactive.findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(note, discount, minimumPurchase, user)
                .map(CustomerDiscountEntity::toCustomerDiscount);
    }

    @Override
    public Mono< CustomerDiscount > create (CustomerDiscount customerDiscount) {
        CustomerDiscountEntity customerDiscountEntity = new CustomerDiscountEntity(customerDiscount);
        customerDiscountEntity.addRegistrationDate();
        return this.readUserByUserMobileNullSafe(customerDiscount.getUser())
                .then(this.customerDiscountReactive.save(customerDiscountEntity))
                .map(CustomerDiscountEntity::toCustomerDiscount);
    }

    private Mono<User> readUserByUserMobileNullSafe(String userMobile) {
        if (userMobile != null) {
            return this.userMicroservice.readByMobile(userMobile);
        } else {
            return Mono.empty();
        }
    }
}
