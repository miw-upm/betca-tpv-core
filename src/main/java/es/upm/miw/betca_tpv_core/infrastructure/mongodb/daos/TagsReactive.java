package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TagsEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TagsReactive extends ReactiveMongoRepository<TagsEntity, String> {
    Mono<TagsEntity> findByName(String name);

    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { name : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { group : {$regex:[1], $options: 'i'} } },"
            + "?#{ [2] == null ? {_id : {$ne:null}} : { description : {$regex:[2], $options: 'i'} } }"
            + "] }")
    Flux<TagsEntity> findByNameAndGroupAndDescriptionNullSafe(
            String name, String group, String description);

    Flux<TagsEntity> findByGroupIsNull();

    @Query("{$and:[" // allow NULL in name
            + "?#{ [0] == null ? {_id : {$ne:null}} : { name : {$regex:[0], $options: 'i'} } },"
            + "{group : {$ne : null}}"
            + "] }")
    Flux<TagsEntity> findByNameLikeAndGroupIsNotNullNullSafe(String name);
}
