package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewPersistence {

    Mono<Review> create(Review review);

    Flux<Review> findByArticleId(String articleId);

    Flux<Review> findByUserId(String userId);

    Mono<Review> update(Review review);

    Mono<Void> deleteById(String id);


    Mono<String> findMostPopularArticle();


}
