package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ComplaintPersistence {

    Flux<Complaint> findByUserMobileNullSafe(String userMobile);

    Flux<Complaint> findByUserMobile(String userMobile);
}
