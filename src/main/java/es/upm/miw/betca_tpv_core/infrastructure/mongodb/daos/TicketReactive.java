package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface TicketReactive extends ReactiveSortingRepository< TicketEntity, String > {

}
