package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.MessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessengerDao extends MongoRepository<MessageEntity, String> {

}
