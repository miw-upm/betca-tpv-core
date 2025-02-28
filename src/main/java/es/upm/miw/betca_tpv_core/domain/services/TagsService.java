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

    private final TagsPersistence tagsPersistence;

    @Autowired
    public TagsService(TagsPersistence tagsPersistence) {
        this.tagsPersistence = tagsPersistence;
    }

    public Mono<Tags> create(Tags tag) {
        return this.tagsPersistence.create(tag);
    }

    public Mono<Tags> readByName(String name) {
        return this.tagsPersistence.readByName(name);
    }

    public Mono<Tags> update(String name, Tags tag) {
        return this.tagsPersistence.readByName(name)
                .map(dataTag -> {
                    BeanUtils.copyProperties(tag, dataTag, "id");
                    return dataTag;
                }).flatMap(dataTag -> this.tagsPersistence.update(name, dataTag));
    }

    public Mono<Void> deleteByName(String name) {
        return this.tagsPersistence.deleteByName(name);
    }

    public Flux<Tags> findByAnyNullField() {
        return this.tagsPersistence.findByAnyNullField();
    }

    public Flux<Tags> findByNameAndGroupAndDescriptionNullSafe(
            String name, String group, String description) {
        return this.tagsPersistence.findByNameAndGroupAndDescriptionNullSafe(
                name, group, description);
    }

    public Flux<Tags> findByNameLikeAndGroupIsNotNullNullSafe(String name) {
        return this.tagsPersistence.findByNameLikeAndGroupIsNotNullNullSafe(name);
    }
}
