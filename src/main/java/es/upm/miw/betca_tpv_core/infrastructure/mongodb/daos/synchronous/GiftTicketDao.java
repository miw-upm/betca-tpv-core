package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.GiftTicketEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GiftTicketDao extends MongoRepository<GiftTicketEntity, String > {
}
