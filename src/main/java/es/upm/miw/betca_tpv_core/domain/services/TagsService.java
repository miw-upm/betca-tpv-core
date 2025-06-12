package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Tags;
import es.upm.miw.betca_tpv_core.domain.persistence.TagsPersistence;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
public class TagsService {


    private final TagPersistence tagPersistence;

    @Autowired
    public TagService(TagPersistence tagPersistence) {
        this.tagPersistence = tagPersistence;
    }

    public Mono<Tag> create(Tag tag) {
        return this.tagPersistence.create(tag);
    }

    public Mono<Tag> read(String id) {
        return this.tagPersistence.readById(id);
    }

    public Flux<Tag> findAll() {
        return this.tagPersistence.findAll();
    }

    public Flux<Tag> findByName(String name) {
        return this.tagPersistence.findByName(name);
    }

    public Flux<Tag> findByGroup(String group) {
        return this.tagPersistence.findByGroup(group);
    }

    public Mono<Tag> update(String id, Tag tag) {
        return this.tagPersistence.readById(id)
                .map(dataTag -> {
                    BeanUtils.copyProperties(tag, dataTag);
                    return dataTag;
                }).flatMap(dataTag -> this.tagPersistence.update(id, dataTag));
    }

    public Mono<Void> delete(String id) {
        return this.tagPersistence.delete(id);
    }


}
