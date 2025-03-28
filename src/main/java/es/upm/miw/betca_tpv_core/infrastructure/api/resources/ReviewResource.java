package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Review;
import es.upm.miw.betca_tpv_core.domain.services.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ReviewResource.REVIEWS)
public class ReviewResource {

    public static final String REVIEWS = "/reviews";

    private final ReviewService reviewService;

    public ReviewResource(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Review> create(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @GetMapping("/article/{articleId}")
    public Flux<Review> findByArticleId(@PathVariable String articleId) {
        return reviewService.findByArticleId(articleId);
    }

    @GetMapping("/user/{userId}")
    public Flux<Review> findByUserId(@PathVariable String userId) {
        return reviewService.findByUserId(userId);
    }

    @PutMapping("/{id}")
    public Mono<Review> update(@PathVariable String id, @RequestBody Review review) {
        review.setId(id);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return reviewService.deleteReview(id);
    }

    @GetMapping("/popular")
    public Mono<String> getMostPopularArticleId() {
        return reviewService.findMostPopularArticleId();
    }
}
