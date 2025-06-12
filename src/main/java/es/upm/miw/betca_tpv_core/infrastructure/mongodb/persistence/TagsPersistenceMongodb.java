package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Tag;
import es.upm.miw.betca_tpv_core.domain.persistence.TagPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TagReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TagEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TagPersistenceMongodb implements TagPersistence {

    private final TagReactive tagReactive;

    @Autowired
    public TagPersistenceMongodb(TagReactive tagReactive) {
        this.tagReactive = tagReactive;
    }

    @Override
    public Mono<Tag> create(Tag tag) {
        return this.tagReactive.save(new TagEntity(tag))
                .map(TagEntity::toTag);
    }

    @Override
    public Mono<Tag> readById(String id) {
        return this.tagReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent tag id: " + id)))
                .map(TagEntity::toTag);
    }

    @Override
    public Flux<Tag> findAll() {
        return this.tagReactive.findAll()
                .map(TagEntity::toTag);
    }

    @Override
    public Flux<Tag> findByName(String name) {
        return this.tagReactive.findByName(name)
                .map(TagEntity::toTag);
    }

    @Override
    public Flux<Tag> findByGroup(String group) {
        return this.tagReactive.findByGroup(group)
                .map(TagEntity::toTag);
    }

    @Override
    public Mono<Tag> update(String id, Tag tag) {
        return this.tagReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent tag id: " + id)))
                .map(tagEntity -> {
                    BeanUtils.copyProperties(tag, tagEntity);
                    return tagEntity;
                })
                .flatMap(this.tagReactive::save)
                .map(TagEntity::toTag);
    }

    @Override
    public Mono<Void> delete(String id) {
        return this.tagReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent tag id: " + id)))
                .flatMap(tagEntity -> this.tagReactive.delete(tagEntity));
    }
}
