package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.InvoiceEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface InvoiceReactive extends ReactiveSortingRepository<InvoiceEntity, String> {

    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : {'ticketEntity': {'$ref':'ticketEntity', '$id':[0] } } }"
            + "] }")
    Flux<InvoiceEntity> findByTicketIdNullSafe(ObjectId ticketId);
}
