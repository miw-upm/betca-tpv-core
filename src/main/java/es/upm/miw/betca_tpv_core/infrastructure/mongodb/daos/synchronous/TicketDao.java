package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;


import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketDao extends MongoRepository< TicketEntity, String > {
}
