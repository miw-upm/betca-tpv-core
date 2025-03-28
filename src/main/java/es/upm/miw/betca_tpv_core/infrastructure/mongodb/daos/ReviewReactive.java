package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ReviewEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReviewReactive extends ReactiveMongoRepository<ReviewEntity, String> {

    Flux<ReviewEntity> findByArticleId(String articleId);

    Flux<ReviewEntity> findByUserId(String userId);
}
