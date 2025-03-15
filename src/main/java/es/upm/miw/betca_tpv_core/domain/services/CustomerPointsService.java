package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.persistence.CustomerPointsPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CustomerPointsService {

    private final CustomerPointsPersistence customerPointsPersistence;
    private final UserMicroservice userMicroservice;
    private static final long ONE_YEAR_MILLIS = 365L * 24 * 60 * 60 * 1000;

    @Autowired
    public CustomerPointsService(CustomerPointsPersistence customerPointsPersistence, UserMicroservice userMicroservice) {
        this.customerPointsPersistence = customerPointsPersistence;
        this.userMicroservice = userMicroservice;
    }

    public Mono<CustomerPoints> createCustomerPoints(CustomerPoints customerPoints) {
        customerPoints.setLastDate(LocalDateTime.now());
        return this.customerPointsPersistence.createCustomerPoints(customerPoints);
    }

    public Mono<CustomerPoints> readCustomerPointsByMobile(String mobile) {
        return this.userMicroservice.readByMobile(mobile)
                .flatMap(user -> this.customerPointsPersistence.readCustomerPointsByMobile(mobile, user))
                .map(cp -> {
                    if (java.time.Duration.between(cp.getLastDate(), LocalDateTime.now()).toMillis() > ONE_YEAR_MILLIS) {
                        cp.setValue(0);
                    }
                    return cp;
                });
    }

    public Mono<CustomerPoints> updateCustomerPoints(String mobile, CustomerPoints customerPoints) {
        return this.userMicroservice.readByMobile(mobile)
                .flatMap(user -> this.customerPointsPersistence.readCustomerPointsByMobile(mobile, user))
                .map(existingPoints -> {
                    BeanUtils.copyProperties(customerPoints, existingPoints, "lastDate");
                    existingPoints.setLastDate(LocalDateTime.now());
                    return existingPoints;
                })
                .flatMap(updated -> this.customerPointsPersistence.updateCustomerPoints(mobile, updated));
    }

    public Mono<CustomerPoints> addCustomerPoints(String mobile, int pointsToAdd) {
        return this.userMicroservice.readByMobile(mobile)
                .flatMap(user -> this.customerPointsPersistence.readCustomerPointsByMobile(mobile, user))
                .map(cp -> {
                    if (java.time.Duration.between(cp.getLastDate(), LocalDateTime.now()).toMillis() > ONE_YEAR_MILLIS) {
                        cp.setValue(0);
                    }
                    cp.setValue(cp.getValue() + pointsToAdd);
                    cp.setLastDate(LocalDateTime.now());
                    return cp;
                })
                .flatMap(cp -> this.customerPointsPersistence.updateCustomerPoints(mobile, cp));
    }

    public Mono<CustomerPoints> useCustomerPoints(String mobile, int pointsToUse) {
        return this.userMicroservice.readByMobile(mobile)
                .flatMap(user -> this.customerPointsPersistence.readCustomerPointsByMobile(mobile, user))
                .map(cp -> {
                    if (java.time.Duration.between(cp.getLastDate(), LocalDateTime.now()).toMillis() > ONE_YEAR_MILLIS) {
                        cp.setValue(0);
                    }
                    int effectivePointsToUse = Math.min(cp.getValue(), pointsToUse);
                    cp.setValue(cp.getValue() - effectivePointsToUse);
                    cp.setLastDate(LocalDateTime.now());
                    return cp;
                })
                .flatMap(cp -> this.customerPointsPersistence.updateCustomerPoints(mobile, cp));
    }
}
