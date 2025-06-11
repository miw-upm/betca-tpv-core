package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintEntity;
import es.upm.miw.betca_tpv_core.domain.model.ComplaintState;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ComplaintReactive extends ReactiveMongoRepository<ComplaintEntity,String> {
    Mono<ComplaintEntity> findByUserMobileAndArticleAndState(String userMobile, ArticleEntity article, ComplaintState complaintState);
    @Query("{$and:["
            + "?#{ [0] == null ? {_id : {$ne:null}} : { userMobile : {$eq : [0]}  } },"
            + "] }")
    Flux<ComplaintEntity> findByUserMobileNullSafe(String userMobile);

    Mono<ComplaintEntity> findByTrackingCode(String trackingCode);
}
