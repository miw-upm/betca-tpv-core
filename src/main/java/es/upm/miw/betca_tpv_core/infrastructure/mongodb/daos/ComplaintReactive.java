package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ComplaintReactive extends ReactiveMongoRepository<ComplaintEntity,String> {
    Flux<ComplaintEntity> findByUserMobileNullSafe(String userMobile);

}
