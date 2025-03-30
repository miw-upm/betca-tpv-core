package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Review;
import es.upm.miw.betca_tpv_core.domain.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = ReviewResource.class)
public class ReviewResourceIT {

    @MockBean
    private ReviewService reviewService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(new ReviewResource(reviewService)).build();
    }

    @Test
    void testCreateReview() {
        Review review = Review.builder()
                .id("1")
                .userId("user1")
                .articleId("article1")
                .stars(5)
                .opinion("Excellent")
                .build();
        Mockito.when(reviewService.createReview(any(Review.class))).thenReturn(Mono.just(review));

        webTestClient.post().uri("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .isEqualTo(review);
    }

    @Test
    void testFindByArticleId() {
        Review review = Review.builder()
                .id("1")
                .userId("user1")
                .articleId("article1")
                .stars(4)
                .opinion("Good")
                .build();
        Mockito.when(reviewService.findByArticleId("article1")).thenReturn(Flux.just(review));

        webTestClient.get().uri("/reviews/article/article1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .contains(review);
    }

    @Test
    void testFindByUserId() {
        Review review = Review.builder()
                .id("1")
                .userId("user1")
                .articleId("article1")
                .stars(4)
                .opinion("Good")
                .build();
        Mockito.when(reviewService.findByUserId("user1")).thenReturn(Flux.just(review));

        webTestClient.get().uri("/reviews/user/user1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .contains(review);
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
        Mockito.when(reviewService.updateReview(any(Review.class))).thenReturn(Mono.just(review));

        webTestClient.put().uri("/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(review)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .isEqualTo(review);
    }

    @Test
    void testDeleteReview() {
        Mockito.when(reviewService.deleteReview("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/reviews/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testGetMostPopularArticleId() {
        String expectedArticleId = "article123";
        Mockito.when(reviewService.findMostPopularArticleId()).thenReturn(Mono.just(expectedArticleId));

        webTestClient.get().uri("/reviews/popular")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedArticleId);
    }
}
