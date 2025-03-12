package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ComplaintPersistence {
    Mono<Complaint> create(Complaint complaint);

    Flux<Complaint> findByUserMobileNullSafe(String userMobile);

    Mono<Complaint> readById(String id);

    Mono<Complaint> findByUserMobileAndBarcode(String userMobile, String barcode);
}
