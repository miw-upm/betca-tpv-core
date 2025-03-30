package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Review;
import es.upm.miw.betca_tpv_core.domain.persistence.ReviewPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReviewServiceIT {

    private ReviewPersistence reviewPersistence;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewPersistence = mock(ReviewPersistence.class);
        reviewService = new ReviewService(reviewPersistence);
    }

    @Test
    void testCreateReviewSuccess() {
        Review review = Review.builder()
                .userId("user1")
                .articleId("article1")
                .stars(5)
                .opinion("Excellent product!")
                .build();
        when(reviewPersistence.create(review)).thenReturn(Mono.just(review));

        Mono<Review> result = reviewService.createReview(review);

        StepVerifier.create(result)
                .expectNextMatches(r -> r.getStars() == 5 &&
                        "Excellent product!".equals(r.getOpinion()))
                .verifyComplete();
        verify(reviewPersistence).create(review);
    }

    @Test
    void testCreateReviewInvalidStars() {
        Review review = Review.builder()
                .userId("user1")
                .articleId("article1")
                .stars(0)
                .opinion("Too low")
                .build();

        Mono<Review> result = reviewService.createReview(review);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
        verify(reviewPersistence, never()).create(any());
    }

    @Test
    void testUpdateReview() {
        Review review = Review.builder()
                .id("1")
                .userId("user1")
                .articleId("article1")
                .stars(3)
                .opinion("Average")
                .build();
        when(reviewPersistence.update(review)).thenReturn(Mono.just(review));

        Mono<Review> result = reviewService.updateReview(review);

        StepVerifier.create(result)
                .expectNextMatches(r -> "Average".equals(r.getOpinion()))
                .verifyComplete();
        verify(reviewPersistence).update(review);
    }

    @Test
    void testDeleteReview() {
        when(reviewPersistence.deleteById("1")).thenReturn(Mono.empty());

        Mono<Void> result = reviewService.deleteReview("1");

        StepVerifier.create(result)
                .verifyComplete();
        verify(reviewPersistence).deleteById("1");
    }

    @Test
    void testFindMostPopularArticleId() {
        String expectedArticleId = "article123";
        when(reviewPersistence.findMostPopularArticle()).thenReturn(Mono.just(expectedArticleId));

        Mono<String> result = reviewService.findMostPopularArticleId();

        StepVerifier.create(result)
                .expectNext(expectedArticleId)
                .verifyComplete();
        verify(reviewPersistence).findMostPopularArticle();
    }
}