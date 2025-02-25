package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.RgpdEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RgpdDao extends MongoRepository<RgpdEntity, String> {
}
