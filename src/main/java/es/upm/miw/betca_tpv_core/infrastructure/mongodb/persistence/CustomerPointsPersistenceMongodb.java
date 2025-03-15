package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.CustomerPointsPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CustomerPointsReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerPointsEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CustomerPointsPersistenceMongodb implements CustomerPointsPersistence {

    private final CustomerPointsReactive customerPointsReactive;

    @Autowired
    public CustomerPointsPersistenceMongodb(CustomerPointsReactive customerPointsReactive) {
        this.customerPointsReactive = customerPointsReactive;
    }

    @Override
    public Mono<CustomerPoints> createCustomerPoints(CustomerPoints customerPoints) {
        CustomerPointsEntity entity = new CustomerPointsEntity();
        BeanUtils.copyProperties(customerPoints, entity);
        entity.setUserMobileNumber(customerPoints.getUser().getMobile());
        entity.setUser(customerPoints.getUser());
        return this.customerPointsReactive.save(entity)
                .map(CustomerPointsEntity::toCustomerPoints);
    }

    @Override
    public Mono<CustomerPoints> readCustomerPointsByMobile(String mobile, User user) {
        return this.customerPointsReactive.readCustomerPointsByUserMobileNumber(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("No points found for mobile: " + mobile)))
                .map(entity -> {
                    entity.setUser(user);
                    return entity.toCustomerPoints();
                });
    }

    @Override
    public Mono<CustomerPoints> updateCustomerPoints(String mobile, CustomerPoints customerPoints) {
        return this.customerPointsReactive.readCustomerPointsByUserMobileNumber(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("No points found for mobile: " + mobile)))
                .flatMap(entity -> {
                    entity.setValue(customerPoints.getValue());
                    entity.setLastDate(customerPoints.getLastDate());
                    if (customerPoints.getUser() != null) {
                        entity.setUserMobileNumber(customerPoints.getUser().getMobile());
                        entity.setUser(customerPoints.getUser());
                    }
                    return this.customerPointsReactive.save(entity)
                            .map(CustomerPointsEntity::toCustomerPoints);
                });
    }
}