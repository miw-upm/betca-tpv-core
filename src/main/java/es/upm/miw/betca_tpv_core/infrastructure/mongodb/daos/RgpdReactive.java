package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.RgpdEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface RgpdReactive extends ReactiveSortingRepository<RgpdEntity, String> {

    Mono<RgpdEntity> findByUserMobile(String userMobile);

}
