package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.GiftTicketEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface GiftTicketReactive extends ReactiveSortingRepository<GiftTicketEntity, String >  {
}
