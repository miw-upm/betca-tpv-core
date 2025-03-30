package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Review;
import es.upm.miw.betca_tpv_core.domain.persistence.ReviewPersistence;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReviewService {

    private final ReviewPersistence reviewPersistence;
    private final ArticleService articleService;
    public ReviewService(ReviewPersistence reviewPersistence, ArticleService articleService) {
        this.reviewPersistence = reviewPersistence;
        this.articleService = articleService;
    }

    public Mono<Review> createReview(Review review) {
        if (review.getStars() < 1 || review.getStars() > 5) {
            return Mono.error(new IllegalArgumentException("Stars must be between 1 and 5"));
        }
        return articleService
                .findByBarcodeAndUserLoggedPurchasedArticlesWithoutComplaintsOpen(review.getArticleId(), review.getUserId())
                .collectList()
                .flatMap(purchasedBarcodes -> {
                    if (purchasedBarcodes.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("User has not purchased this article"));
                    }
                    return reviewPersistence.create(review);
                });
    }

    public Flux<Review> findByArticleId(String articleId) {
        return reviewPersistence.findByArticleId(articleId);
    }

    public Flux<Review> findByUserId(String userId) {
        return reviewPersistence.findByUserId(userId);
    }

    public Mono<Review> updateReview(Review review) {
        if (review.getStars() < 1 || review.getStars() > 5) {
            return Mono.error(new IllegalArgumentException("Stars must be between 1 and 5"));
        }
        return reviewPersistence.update(review);
    }

    public Mono<Void> deleteReview(String id) {
        return reviewPersistence.deleteById(id);
    }

    public Mono<String> findMostPopularArticleId() {
        return reviewPersistence.findMostPopularArticle();
    }
}
