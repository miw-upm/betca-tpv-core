package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface TicketReactive extends ReactiveSortingRepository< TicketEntity, String > {

    Flux<TicketEntity> findByIdLikeOrReferenceLikeOrUserMobileLike(String id, String reference, String userMobile);
}
