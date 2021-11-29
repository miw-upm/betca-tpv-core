package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;


import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CashierEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CashierDao extends MongoRepository< CashierEntity, String > {
    Optional< CashierEntity > findFirstByOrderByOpeningDateDesc();
}
