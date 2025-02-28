package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Tags;
import es.upm.miw.betca_tpv_core.domain.persistence.TagsPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TagsReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TagsEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TagsPersistenceMongodb implements TagsPersistence {

    private final TagsReactive tagsReactive;

    @Autowired
    public TagsPersistenceMongodb(TagsReactive tagsReactive) {
        this.tagsReactive = tagsReactive;
    }

    @Override
    public Mono<Tags> create(Tags tag) {
        return this.assertNameNotExist(tag.getName())
                .then(Mono.justOrEmpty(tag))
                .map(TagsEntity::new)
                .flatMap(this.tagsReactive::save)
                .map(TagsEntity::toTag);
    }

    @Override
    public Mono<Tags> readByName(String name) {
        return this.tagsReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent tag name: " + name)))
                .map(TagsEntity::toTag);
    }

    @Override
    public Mono<Tags> update(String name, Tags tag) {
        Mono<TagsEntity> tagsEntityMono;
        if (!name.equals(tag.getName())) {
            tagsEntityMono = this.assertNameNotExist(tag.getName())
                    .then(this.tagsReactive.findByName(name));
        } else {
            tagsEntityMono = this.tagsReactive.findByName(name);
        }
        return tagsEntityMono
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent tag name: " + name)))
                .flatMap(tagsEntity -> {
                    BeanUtils.copyProperties(tag, tagsEntity);
                    return this.tagsReactive.save(tagsEntity);
                })
                .map(TagsEntity::toTag);
    }

    @Override
    public Mono<Void> deleteByName(String name) {
        return this.tagsReactive.findByName(name)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent tag name: " + name)))
                .flatMap(tagsEntity -> this.tagsReactive.delete(tagsEntity));
    }

    @Override
    public Flux<Tags> findByAnyNullField() {
        return this.tagsReactive.findByGroupIsNull()
                .map(TagsEntity::toTag);
    }

    @Override
    public Flux<Tags> findByNameAndGroupAndDescriptionNullSafe(String name, String group, String description) {
        return this.tagsReactive.findByNameAndGroupAndDescriptionNullSafe(name, group, description)
                .map(TagsEntity::toTag);
    }

    @Override
    public Flux<Tags> findByNameLikeAndGroupIsNotNullNullSafe(String name) {
        return this.tagsReactive.findByNameLikeAndGroupIsNotNullNullSafe(name)
                .map(TagsEntity::toTag);
    }

    private Mono<Void> assertNameNotExist(String name) {
        return this.tagsReactive.findByName(name)
                .flatMap(tagsEntity -> Mono.error(
                        new ConflictException("Tag name already exists: " + name)
                ));
    }
}
