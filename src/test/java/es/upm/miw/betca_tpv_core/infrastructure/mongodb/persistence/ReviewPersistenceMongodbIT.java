package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Review;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ReviewReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ReviewEntity;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReviewPersistenceMongodbIT {

    private ReviewReactive reviewReactive;
    private ReactiveMongoTemplate mongoTemplate;
    private ReviewPersistenceMongodb reviewPersistence;

    @BeforeEach
    void setUp() {
        reviewReactive = mock(ReviewReactive.class);
        mongoTemplate = mock(ReactiveMongoTemplate.class);
        reviewPersistence = new ReviewPersistenceMongodb(reviewReactive, mongoTemplate);
    }

    @Test
    void testCreateReview() {
        Review review = Review.builder()
                .userId("user1")
                .articleId("article1")
                .stars(4)
                .opinion("Good")
                .build();
        ReviewEntity savedEntity = ReviewEntity.builder()
                .id("id123")
                .userId("user1")
                .articleId("article1")
                .stars(4)
                .opinion("Good")
                .build();
        when(reviewReactive.save(any(ReviewEntity.class))).thenReturn(Mono.just(savedEntity));

        Mono<Review> result = reviewPersistence.create(review);

        StepVerifier.create(result)
                .expectNextMatches(r -> "id123".equals(r.getId())
                        && "Good".equals(r.getOpinion()))
                .verifyComplete();
        verify(reviewReactive).save(any(ReviewEntity.class));
    }

    @Test
    void testFindByArticleId() {
        ReviewEntity entity = ReviewEntity.builder()
                .id("id123")
                .userId("user1")
                .articleId("article1")
                .stars(4)
                .opinion("Good")
                .build();
        when(reviewReactive.findByArticleId("article1")).thenReturn(Flux.just(entity));

        Flux<Review> result = reviewPersistence.findByArticleId("article1");

        StepVerifier.create(result)
                .expectNextMatches(r -> "article1".equals(r.getArticleId()))
                .verifyComplete();
        verify(reviewReactive).findByArticleId("article1");
    }

    @Test
    void testUpdateReview() {
        Review review = Review.builder()
                .id("id123")
                .userId("user1")
                .articleId("article1")
                .stars(3)
                .opinion("Average")
                .build();
        ReviewEntity entity = ReviewEntity.builder()
                .id("id123")
                .userId("user1")
                .articleId("article1")
                .stars(3)
                .opinion("Average")
                .build();
        when(reviewReactive.save(any(ReviewEntity.class))).thenReturn(Mono.just(entity));

        Mono<Review> result = reviewPersistence.update(review);

        StepVerifier.create(result)
                .expectNextMatches(r -> "Average".equals(r.getOpinion()))
                .verifyComplete();
        verify(reviewReactive).save(any(ReviewEntity.class));
    }

    @Test
    void testDeleteReview() {
        when(reviewReactive.deleteById("id123")).thenReturn(Mono.empty());

        Mono<Void> result = reviewPersistence.deleteById("id123");

        StepVerifier.create(result)
                .verifyComplete();
        verify(reviewReactive).deleteById("id123");
    }

    @Test
    void testFindMostPopularArticle() {
        Document doc = new Document();
        doc.put("articleId", "articleMostPopular");

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("reviews"), eq(Document.class)))
                .thenReturn(Flux.just(doc));

        Mono<String> result = reviewPersistence.findMostPopularArticle();

        StepVerifier.create(result)
                .expectNext("articleMostPopular")
                .verifyComplete();
        verify(mongoTemplate).aggregate(any(Aggregation.class), eq("reviews"), eq(Document.class));
    }
}
