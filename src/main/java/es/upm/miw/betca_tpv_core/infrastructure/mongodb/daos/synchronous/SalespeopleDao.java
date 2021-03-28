package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.SalespeopleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SalespeopleDao extends MongoRepository<SalespeopleEntity,String> {
}
