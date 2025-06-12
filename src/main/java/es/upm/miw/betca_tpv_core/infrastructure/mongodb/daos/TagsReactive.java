package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TagsEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TagsReactive extends ReactiveMongoRepository<TagsEntity, String> {
  Mono<TagEntity> findById(String id);
    
    Flux<TagEntity> findByName(String name);
    
    Flux<TagEntity> findByGroup(String group);
}
