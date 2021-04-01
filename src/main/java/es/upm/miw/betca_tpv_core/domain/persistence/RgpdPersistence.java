package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RgpdPersistence {

    Mono<Rgpd> readByMobile(String mobile);

    Mono<Rgpd> create(Rgpd rgpd);

    Mono<Rgpd> update(String mobile, Rgpd rgpd);

}
