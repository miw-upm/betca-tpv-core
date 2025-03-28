package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Review;
import es.upm.miw.betca_tpv_core.domain.persistence.ReviewPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ReviewReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ReviewEntity;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ReviewPersistenceMongodb implements ReviewPersistence {

    private final ReviewReactive reviewReactive;
    private final ReactiveMongoTemplate mongoTemplate;

    public ReviewPersistenceMongodb(ReviewReactive reviewReactive, ReactiveMongoTemplate mongoTemplate) {
        this.reviewReactive = reviewReactive;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Review> create(Review review) {
        return reviewReactive.save(toEntity(review))
                .map(this::toModel);
    }

    @Override
    public Flux<Review> findByArticleId(String articleId) {
        return reviewReactive.findByArticleId(articleId)
                .map(this::toModel);
    }

    @Override
    public Flux<Review> findByUserId(String userId) {
        return reviewReactive.findByUserId(userId)
                .map(this::toModel);
    }

    @Override
    public Mono<Review> update(Review review) {
        return reviewReactive.save(toEntity(review))
                .map(this::toModel);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return reviewReactive.deleteById(id);
    }

    @Override
    public Mono<String> findMostPopularArticle() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("articleId").avg("stars").as("avgStars"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "avgStars")),
                Aggregation.limit(1),
                Aggregation.project().and("_id").as("articleId")
        );

        return mongoTemplate.aggregate(aggregation, "reviews", Document.class)
                .next()
                .map(doc -> doc.getString("articleId"));
    }


    private ReviewEntity toEntity(Review model) {
        if (model == null) {
            return null;
        }
        return ReviewEntity.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .articleId(model.getArticleId())
                .stars(model.getStars())
                .opinion(model.getOpinion())
                .build();
    }

    private Review toModel(ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        return Review.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .articleId(entity.getArticleId())
                .stars(entity.getStars())
                .opinion(entity.getOpinion())
                .build();
    }
}