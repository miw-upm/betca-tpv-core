package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerPointsPersistence {

    Mono<CustomerPoints> createCustomerPoints(CustomerPoints customerPoints);

    Mono<CustomerPoints> readCustomerPointsByMobile(String mobile, User user);

    Mono<CustomerPoints> updateCustomerPoints(String mobile, CustomerPoints customerPoints);

}
