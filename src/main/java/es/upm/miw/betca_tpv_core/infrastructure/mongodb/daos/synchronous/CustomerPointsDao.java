package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerPointsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerPointsDao extends MongoRepository<CustomerPointsEntity, String> {
    List<CustomerPointsEntity> readCustomerPointsByUserMobileNumber(String userMobileNumber);
}
