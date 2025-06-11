package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.persistence.CustomerDiscountPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CustomerDiscountReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CustomerDiscountPersistenceMongodb implements CustomerDiscountPersistence {
    private final CustomerDiscountReactive customerDiscountReactive;

    @Autowired
    public CustomerDiscountPersistenceMongodb(CustomerDiscountReactive customerDiscountReactive) {
        this.customerDiscountReactive = customerDiscountReactive;
    }

    @Override
    public Mono<CustomerDiscount> createCustomerDiscount(CustomerDiscount customerDiscount) {
        CustomerDiscountEntity customerDiscountEntity = customerDiscount.toEntity();
        return this.customerDiscountReactive.save(customerDiscountEntity)
                .map(CustomerDiscountEntity::toCustomerDiscount);
    }

    @Override
    public Mono<CustomerDiscount> readByUserMobile(String userMobile){
        return this.customerDiscountReactive.readByUserMobile(userMobile)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent user with userMobile: " + userMobile)))
                .map(CustomerDiscountEntity::toCustomerDiscount);
    }

    @Override
    public Mono<CustomerDiscount> updateCustomerDiscount(String userMobile, CustomerDiscount customerDiscount) {
        if (!userMobile.equals(customerDiscount.getUser().getMobile())) {
            return Mono.error(new ConflictException("Inconsistent user mobile"));
        }

        return this.customerDiscountReactive.readByUserMobile(userMobile)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent stock alarm name: " + userMobile)))
                .flatMap(customerDiscountEntity -> {
                    customerDiscountEntity.setUser(customerDiscount.getUser());
                    customerDiscountEntity.setRegistrationDate(customerDiscount.getRegistrationDate());
                    customerDiscountEntity.setNote(customerDiscount.getNote());
                    customerDiscountEntity.setDiscount(customerDiscount.getDiscount());
                    customerDiscountEntity.setMinimumPurchase(customerDiscount.getMinimumPurchase());
                    return this.customerDiscountReactive.save(customerDiscountEntity);
                })
                .map(CustomerDiscountEntity::toCustomerDiscount);
    }

    @Override
    public Flux<CustomerDiscount> findAll() {
        return this.customerDiscountReactive.findAll().map(CustomerDiscountEntity::toCustomerDiscount);
    }

    @Override
    public Mono<Void> deleteByUserMobile(String userMobile) {
        return this.customerDiscountReactive.deleteByUserMobile(userMobile);
    }

}
