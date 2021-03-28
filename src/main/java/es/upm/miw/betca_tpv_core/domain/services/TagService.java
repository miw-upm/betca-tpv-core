package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Tag;
import es.upm.miw.betca_tpv_core.domain.persistence.TagPersistence;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TagService {

    private TagPersistence tagPersistence;

    @Autowired
    public TagService(TagPersistence tagPersistence) {
        this.tagPersistence = tagPersistence;
    }

    public Mono<Tag> create(Tag tag) {
        return this.tagPersistence.create(tag);
    }

    public Mono<Tag> update(String name, Tag tag) {
        return this.tagPersistence.readByName(name)
                .map(dataTag -> {
                    BeanUtils.copyProperties(tag, dataTag);
                    return dataTag;
                }).flatMap(dataTag -> this.tagPersistence.update(name, dataTag));
    }
    public Mono<Tag> read(String name) {
        return this.tagPersistence.readByName(name);
    }
    public Flux<Tag> findByNameAndGroupAndDescriptionNullSafe(
            String name, String group, String description) {
        return this.tagPersistence.findBNameAndGroupAndDescriptionNullSafe(
                name, group, description);
    }
}
