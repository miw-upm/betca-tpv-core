package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ComplaintReactive extends ReactiveMongoRepository<ComplaintEntity,String> {
    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { userMobile : {$regex:[0], $options: 'i'} } },"
            + "] }")
    Flux<ComplaintEntity> findByUserMobileNullSafe(String userMobile);

}
