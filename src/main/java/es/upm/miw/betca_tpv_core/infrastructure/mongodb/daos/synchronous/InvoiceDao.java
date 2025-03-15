package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.InvoiceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceDao  extends MongoRepository<InvoiceEntity, String> {
}
